# Performance

## Compile-time Overhead

Compilation time for table definitions increases with the number of columns

<p align="center">Create compile time</p>
<img src="../img/compile_create.png">

Compile time for query construction increases with the number of columns to select

<p align="center">Create query compile time</p>
<img src="../img/compile_create_query.png">

## Runtime Overhead

Since ldbc uses Tuple internally, it is much slower than pure class definition.

<p align="center">Create runtime</p>
<img src="../img/runtime_create.png">

ldbc is much slower than the others with table definitions.

<p align="center">Create query runtime</p>
<img src="../img/runtime_create_query.png">

## Query execution Overhead

Throughput of select query execution decreases as the number of records to retrieve increases.

<p align="center">Select Throughput</p>
<img src="../img/select_throughput.png">

Throughput of insert query execution decreases as the number of records to insert increases.

※ Not accurate because the query performed is not an exact match.

<p align="center">Insert Throughput</p>
<img src="../img/insert_throughput.png">
