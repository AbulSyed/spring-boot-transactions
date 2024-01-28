### Spring boot transactions

- Atomicity - all transactions should finish succesfully & if 1 transaction fails, there should be a rollback so we don't have corrupt data
- Consistency - database should have constraints in place to enable consistent data. e.g. `price` column should be positive
- Isolation - ensure data not corrupted due to concurrent transactions (can be achieved by database locking `for update`)
Durability - committed transactions to be persisted in non volatile memory

### Aspect

Code that intercepts a method or group of methods & executes when specified. e.g. before, after, before a exception thrown, after execption thrown etc..

### @Transactional

When you annotate a class/method with @Transactional - the PlatformTransactionManager will manage the execution of the transaction & if there is a RuntimeException (unchecked) thrown AND IS PROPAGATED OUTSIDE OF METHOD, the transaction will be rolled back.

#### rollbackFor & noRollbackFor

By default only unchecked exceptions (RuntimeExceptions) are rolled back. If you're throwing a checked exception, you need to specify it explicitly using: `@Transactional(rollbackFor = Exception.class)`

We can also decide to not roll back e.g. using noRollbackFor

#### Propagation

1. Propagation.REQUIRED (default) - Support a current transaction, create a new one if none exists

`addTenProduct` will create the transaction & `productRepository.save()` will use it. the transaction will then commit at the end of `addTenProduct`.

so in the case the exception is thrown, the 4 items saved will be rolled back

```
@Transactional
public void addTenProduct() { // creates
  for (int i = 1; i <= 10; i++) {
    ProductEntity product = new ProductEntity();
    product.setName("product " + i);

    productRepository.save(product);

    if (i == 5) {
      throw new RuntimeException("something went wrong");
    }
  }
} // commit


public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

  @Transactional
  @Modifying
  @Query(value = """
    INSERT INTO products (name)
    VALUES (:name)
  """, nativeQuery = true)
  void saveProduct(String name);
}
```

2. Propagation.REQUIRES_NEW - Create a new transaction, & suspend the current transaction if one exists

`productRepository.save()` will always create a new transaction. in the `addTenProduct` transaction - no sql query was executed, hence nothing will be rolled back & 5 products will be saved due to `productRepository.save()` & then a execption will be thrown.

```
@Transactional
public void addTenProduct() {
  for (int i = 1; i <= 10; i++) {
    ProductEntity product = new ProductEntity();
    product.setName("product " + i);

    productRepository.save(product);

    if (i == 5) {
      throw new RuntimeException("something went wrong");
    }
  }
}


public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  @Modifying
  @Query(value = """
    INSERT INTO products (name)
    VALUES (:name)
  """, nativeQuery = true)
  void saveProduct(String name);
}
```

3. Propagation.MANDATORY - Support a current transaction, throw an exception if none exists

```
IllegalTransactionStateException: No existing transaction found for transaction marked with propagation 'mandatory'
```

4. Propagation.NEVER - Execute non-transactionally, throw an exception if a transaction exists

```
IllegalTransactionStateException: Existing transaction found for transaction marked with propagation 'never'
```

5. Propagation.SUPPORTS - Support a current transaction, execute non-transactionally if none exists

6. Propagation.NOT_SUPPORTED - Execute non-transactionally, suspend the current transaction if one exists

7. Propagation.NESTED - Execute within a nested transaction if a current transaction exists, behave like REQUIRED otherwise

#### Isolation

Isolation, refers to the degree to which the operations within a transaction are isolated from the effects of other concurrently executing transactions

- Dirty Read:

Occurs when a transaction reads data that has been modified by another transaction but not yet committed. If the transaction that made the modification is later rolled back, the data read by the first transaction becomes invalid.

- Phantom Read:

Occurs when a transaction reads a set of records that satisfy a certain condition, but another transaction inserts or deletes records that also satisfy the condition, causing the first transaction to see a different set of records when re-executed.

e.g. Transaction A reads all records where the value of a certain column is greater than 100. While Transaction A is still executing, Transaction B inserts a new record with a value greater than 100. When Transaction A reads the records again, it sees the newly inserted record, which wasn't visible before.

- Repeatable Read:

Occur when a transaction re-reads data it has previously read & finds that data has been modified by another transaction in the meantime.

Unlike dirty reads & phantom reads, repeatable reads involve reading the same data multiple times within the same transaction. If another transaction modifies the data between the first & second reads, the transaction might observe different values for the same data.

1. Isolation.DEFAULT - uses the default isolation level of the underlying data store.

2. Isolation.READ_UNCOMMITTED - lowest isolation level. allows a transaction to read data that has been modified but not yet committed by other transactions. offers the highest level of concurrency but the lowest level of data consistency

3. Isolation.READ_COMMITTED - ensures that a transaction only sees data that has been committed by other transactions. prevents dirty reads (reading uncommitted data) but allows non-repeatable reads & phantom reads

4. Isolation.REPEATABLE_READ - ensures that if a transaction reads a piece of data, it will get the same value if it reads that data again within the same transaction. prevents non-repeatable reads but allows phantom reads

5. Isolation.SERIALIZABLE - highest isolation level. ensures that transactions are completely isolated from each other, meaning that no other transactions can access data that has been read or modified by the current transaction until it's committed. this level provides the highest level of data consistency but may lead to decreased concurrency

READ UNCOMMITTED allows dirty reads, phantom reads, & non-repeatable reads.
READ COMMITTED prevents dirty reads but allows phantom reads & non-repeatable reads.
REPEATABLE READ prevents dirty reads & non-repeatable reads reads but allows phantom reads.
SERIALIZABLE prevents all three anomalies, ensuring the highest level of data consistency but potentially reducing concurrency.

Higher isolation levels provide stronger consistency guarantees but may impact concurrency & performance. Conversely, lower isolation levels offer better concurrency but may lead to potential data anomalies.