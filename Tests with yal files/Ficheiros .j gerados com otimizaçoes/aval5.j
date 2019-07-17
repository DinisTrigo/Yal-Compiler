.class public aval5
.super java/lang/Object

.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static f(II)I
.limit stack 25
.limit locals 25
B1:
bipush 10
istore 2
block0:
iload_0
iload_1
if_icmpeq block0true
block4:
iload_1
iload_0
iadd
istore 1
goto block5
block0true:
block1loop:
iload_0
iload_2
if_icmplt block1branch
goto block3
block1branch:
block2:
iload_0
iconst_1
iadd
istore 0
iload_0
iload_2
if_icmplt block1branch
block3:
iload_0
iconst_2
ishl
istore 1
block5:
iload 1
ireturn
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 20
.limit locals 20
B1:
iconst_4
iconst_5
invokestatic aval5/f(II)I
istore 2
iload_2
invokestatic io/println(I)V
iconst_2
iconst_2
invokestatic aval5/f(II)I
istore 2
iload_2
invokestatic io/println(I)V
return
.end method

