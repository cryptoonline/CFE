CFE
===

The repository contains code for the superfast Controlled Functional Encryption (CFE) construction that allows to compute the actual value of inner product of two vectors (and not the orthogonality testing). Currently, the repository do not have the code for our general construction. Both superfast and general CFE constructions were proposed in the following paper:

[**Controlled Functional Encryption**](https://web.engr.illinois.edu/~naveed2/pub/CCS2014CFE.pdf). Muhammad Naveed, Shashank Agrawal, Manoj Prabhakaran, Xiaofeng Wang, Erman Ayday, Jean-Pierre Hubaux and Carl A. Gunter. *In ACM Conference on Computer and Communications Security (CCS), 2014*.

Compilation
------------
To compile the code, run following command from the main repository directory (i.e. CFE):

    javac -cp "src:lib/bcprov-jdk15on-151.jar" src/Test.java

Execution
----------
To run the code use the following command (note that command line arguments are needed):

    java -cp "src:lib/bcprov-jdk15on-151.jar" Test <Size of the data vector> <Size of the function vector> <Key size> <Iterations>

Choose one of the following as key size 1024, 2048, 3072, or 4096.

Example:

    java -cp "src:lib/bcprov-jdk15on-151.jar" Test 20000 100 2048 10
