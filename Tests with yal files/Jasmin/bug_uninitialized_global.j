.class public bug_uninitialized_global
.super java/lang/Object
.field public static c [I 

.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method static public <clinit>()V
ldc 95
newarray int
putstatic bug_uninitialized_global/c [I
return
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 9999
.limit locals 9999
B1:
getstatic bug_uninitialized_global/c [I
arraylength
istore 1
ldc 0
istore 2
block0loopstart:
iload 2
iload 1
if_icmplt block0body
goto block0end
block0body:
getstatic bug_uninitialized_global/c [I
iload 2
getstatic bug_uninitialized_global/null [I
arraylength
iastore
iload 2
ldc 1
iadd
istore 2
goto block0loopstart
block0end:
return
.end method

