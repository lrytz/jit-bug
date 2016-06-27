JIT-bug

Run `mvn compile exec:java`. If the bug occurs you will see output of the following shape
(you might have to run the command multiple times until the bug shows up):

```
[INFO] --- exec-maven-plugin:1.5.0:java (default-cli) @ jit-bug ---
 >> Found null: Thread-1 at step: 3937 res:  JTuple6{t1=null, t2=192837934738, t3=null, t4=null, t5=null, t6=0}
 >> Found null: Thread-2 at step: 4975 res:  JTuple6{t1=null, t2=192837934738, t3=null, t4=null, t5=null, t6=0}
 >> Found null: Thread-8 at step: 4513 res:  JTuple6{t1=null, t2=192837934738, t3=null, t4=null, t5=null, t6=0}
...
```

The `t3` value should always be non-null.

The bug only appears when the JIT is enabled. This can be verified by running
`MAVEN_OPTS="-Xint" mvn compile exec:java`, which never fails.

The bug was initially reported at https://issues.scala-lang.org/browse/SI-9828 with a dependency on the Scala library jar.
However, the current version in this repository is Java-only and does not have any dependencies.

My environment:

```
$ java -version
java version "1.8.0_92"
Java(TM) SE Runtime Environment (build 1.8.0_92-b14)
Java HotSpot(TM) 64-Bit Server VM (build 25.92-b14, mixed mode)
$ uname -a
Darwin lucmac.local 15.5.0 Darwin Kernel Version 15.5.0: Tue Apr 19 18:36:36 PDT 2016; root:xnu-3248.50.21~8/RELEASE_X86_64 x86_64
```
