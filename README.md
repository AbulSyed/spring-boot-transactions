### Spring boot transactions

- Atomicity - all transactions should finish succesfully & if 1 transaction fails, there should be a rollback so we don't have corrupt data
- Consistency - database should have constraints in place to enable consistent data. e.g. `price` column should be positive
- Isolation - ensure data not corrupted due to concurrent transactions (can be achieved by database locking `for update`)
Durability - committed transactions to be persisted in non volatile memory


The below code will insert 7 out of 10 items in the database. This goes against ACID principles.

```
public void saveProduct() {
for (int i = 1; i <= 10; i++) {
  Product product = new Product();
  product.setId(i);
  product.setName("item: " + i);

  productRepository.saveProduct(product);

    if (i == 7) {
      throw new RuntimeException("i is 7");
    }
  }
}
```

### Aspect

Code that intercepts a method or group of methods

### @Transactional

When an exception is thrown within a method annotated with @Transactional, it should trigger a rollback of the transaction, which means any changes made within that transaction should be reverted.

Spring's default behavior is to only rollback unchecked exceptions (RuntimeExceptions and subclasses). If you're throwing a checked exception, you need to specify it explicitly using:

`@Transactional(rollbackFor = Exception.class)`

