.class public aval8
.super java/lang/Object

.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static max1()I
.limit stack 35
.limit locals 35
B1:
invokestatic io/read()I
istore 1
invokestatic io/read()I
istore 2
iload_2
istore 2
block0:
iload_1
iload_2
if_icmpgt block0true
goto block2
block0true:
block1:
iload_1
istore 2
block2:
bipush 8
istore 0
ldc "a"
iload_1
invokestatic io/print(Ljava/lang/String;I)V
block3:
iload_1
bipush -23
if_icmplt block3true
block5:
bipush -8
istore 1
goto block6
block3true:
block4:
iconst_0
istore 1
block6:
iload_1
istore 2
iload 2
ireturn
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 20
.limit locals 20
B1:
invokestatic aval8/max1()I
istore 2
iload_2
invokestatic io/println(I)V
return
.end method

