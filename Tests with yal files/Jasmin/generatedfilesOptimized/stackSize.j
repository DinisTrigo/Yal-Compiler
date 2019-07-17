.class public stackSize
.super java/lang/Object

.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static f(I)V
.limit stack 15
.limit locals 15
B1:
block0:
iload_0
iload_0
iload_0
iload_0
iload_0
invokestatic stackSize/h(IIII)I
if_icmpgt block0true
block2:
ldc "Not greater"
invokestatic io/println(Ljava/lang/String;)V
goto block3
block0true:
block1:
ldc "Greater"
invokestatic io/println(Ljava/lang/String;)V
block3:
return
.end method

.method public static g(I)I
.limit stack 20
.limit locals 20
B1:
iload_0
iload_0
iload_0
iload_0
iload_0
invokestatic stackSize/h(IIII)I
imul
istore 1
iload 1
ireturn
.end method

.method public static h(IIII)I
.limit stack 35
.limit locals 35
B1:
iload_0
iload_1
iadd
istore 4
iload 4
iload_2
iadd
istore 4
iload 4
iload_3
iadd
istore 4
iload 4
ireturn
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 20
.limit locals 20
B1:
iconst_m1
istore 2
iconst_m1
invokestatic stackSize/f(I)V
return
.end method

