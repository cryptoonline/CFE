CFE
===

Code for superfast Controlled Functional Encryption (CFE) construction.

To compile the code, run the following command from the main repository directory (CFE):
javac -cp "src:lib/bcprov-jdk15on-151.jar" src/Test.java

To run the code use the following format:
java -cp "src:lib/bcprov-jdk15on-151.jar" Test <Size of the data vector> <Size of the function vector> <Key size> <Iterations>

Choose one of the following as key size 1024, 2048, 3072, or 4096.

Example: java -cp "src:lib/bcprov-jdk15on-151.jar" Test 20000 100 2048 10
