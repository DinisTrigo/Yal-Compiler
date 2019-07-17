.class public aval7
.super java/lang/Object

.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static Count(I)I
.limit stack 30
.limit locals 30
B1:
iconst_0
istore 2
iconst_m1
istore 3
block0loop:
iload_3
bipush 32
if_icmplt block0branch
goto block5
block0branch:
block1:
iload_0
iconst_1
iand
istore 1
block2:
iload_1
iconst_1
if_icmpeq block2true
goto block4
block2true:
block3:
iload_2
iconst_1
iadd
istore 2
block4:
iload_0
iconst_1
ishr
istore 0
iload_3
iconst_1
iadd
istore 3
iload_3
bipush 32
if_icmplt block0branch
block5:
iload 2
ireturn
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 20
.limit locals 20
B1:
iconst_3
invokestatic aval7/Count(I)I
istore 2
iload_2
invokestatic io/println(I)V
return
.end method

