# SQL as Functional

[![Build Status](https://travis-ci.com/time1015/sql-as-functional.svg?branch=master)](https://travis-ci.com/time1015/sql-as-functional)

A Java Library that wraps DataSources and exposes Connections in a handler-like fashion. This ensures manageability and isolation of control of Connections.

## Usage

### Set up a `ConnectionFactory`

If you have a `javax.sql.DataSource`, you can use its `getConnection` method:
```
DataSource ds = ... ;

ConnectionFactory factoryFromDataSource = ds::getConnection;
```

If you have a `javax.sql.PooledConnection`, you can also use its `getConnection` method:
```
ConnectionPoolDataSource cpds = ... ;
PooledConnection pc = cpds.getPooledConnection();

ConnectionFactory factoryFromConnectionPool = pc::getConnection;
```

It is **NOT** recommended to wrap a bare `java.sql.Connection` instance, because it will be closed after use by the `SqlEndpoint` class that takes the factory that wraps around it.
```
Connection dbConnection = ... ;

ConnectionFactory factoryFromConnection = () -> dbConnection; // connection will be closed by SqlEndpoint after use
```

### Set up a `SqlEndpoint`

Create a `SqlEndpoint` with the given `ConnectionFactory`.
```
ConnectionFactory factory = ... ;
SqlEndpoint endpoint = new SqlEndpoint(factory);
```

You may also provide an optional default `ExceptionHandler` for the endpoint.
```
ConnectionFactory factory = ... ;
ExceptionHandler handler = ... ;

SqlEndpoint endpoint = new SqlEndpoint(factory, handler);
```

### Set up a `ConnectionHandler`

Create a `ConnectionHandler` to carry out tasks that would make use of the connection, such as querying and/or committing transactions to the destination data source.
```
ConnectionHandler.OfVoid connHandler = conn -> {
  Statement stmt = conn.createStatement();
  stmt.execute("DELETE FROM Employees WHERE First_Name = \"John\" AND Last_Name = \"Doe\"");

  stmt.close();
};
```

`ConnectionHandler`s can also return values, making it easier to pass data over the boundary between the handler and the caller.
```
ConnectionHandler<List<Employee>> handlerForFetchingEmployees = conn -> {
  Statement stmt = conn.createStatement();
  stmt.execute("SELECT * FROM Employees");

  List<Employee> employees = new ArrayList<>();
  ResultSet rs = stmt.getResultSet();
  while (rs.hasNext())
    employees.add(
      new Employee(
        rs.getString("First_Name"),
        rs.getString("Last_Name")
      )
    );

  rs.close();
  stmt.close();
  return employees;
};
```

In the event on an exception (such as a data source error (`SQLException`) or otherwise), `ConnectionHandler`s can also define a default value to be returned to the caller.
```
ConnectionHandler<List<Employee>> handlerForFetchingEmployees = new ConnectionHandler<List<Employee>>() {
  @Override
  public List<Employee> handleAndReturn(Connection connection) throws Exception {
    Statement stmt = conn.createStatement();
    stmt.execute("SELECT * FROM Employees");

    List<Employee> employees = new ArrayList<>();
    ResultSet rs = stmt.getResultSet();
    while (rs.hasNext())
      employees.add(
        new Employee(
          rs.getString("First_Name"),
          rs.getString("Last_Name")
        )
      );

    rs.close();
    stmt.close();
    return employees;
  }

  @Override
  public List<Employee> defaultValue() {
    return List.of(); // return an empty list in case an exception occurred
  }
}
```

## Establish a connection from the endpoint

Call the `connect` method of the `SqlEndpoint` created earlier, and provide the `ConnectionHandler` to handle the connection to be established.
```
SqlEndpoint endpoint = ... ;
ConnectionHandler.OfVoid handler = ... ;

endpoint.connect(handler);
```

If the `ConnectionHandler` returns a value, the caller may catch and assign that returned value.
```
SqlEndpoint endpoint = ... ;
ConnectionHandler<List<Employee>> listEmployees = ... ;

List<Employee> employees = endpoint.connect(listEmployees);
```

Any exception that occurs will be caught and sent to the default `ExceptionHandler` set for the endpoint. If no default was set, the endpoint simply throws the exception as a `SqlEndpointException` back to the caller.

You may also provide an optional `ExceptionHandler` to handle this connection for any exception that may occur. This will override the default `ExceptionHandler` of the `SqlEndpoint` (if it was set) for this particular connection.
```
SqlEndpoint endpoint = ... ;
ExceptionHandler exHandler = ex -> {
  ex.printStackTrace(System.err);
};
ConnectionHandler<List<Employee>> listEmployees = ... ;

List<Employee> employees = endpoint.connect(listEmployees, exHandler);
```
