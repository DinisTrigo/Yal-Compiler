.class public aval3
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
block0:
iload_0
iload_1
if_icmpge block0true
block2:
iconst_4
istore 2
goto block3
block0true:
block1:
iconst_2
istore 2
block3:
iload 2
ireturn
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 25
.limit locals 25
B1:
iconst_2
istore 2
iconst_3
istore 3
iload_2
iload_3
invokestatic aval3/f(II)I
istore 2
iload_2
invokestatic io/println(I)V
bipush 6
istore 2
iload_2
iload_3
invokestatic aval3/f(II)I
istore 2
iload_2
invokestatic io/println(I)V
return
.end method

