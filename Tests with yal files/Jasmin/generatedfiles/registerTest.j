.class public registerTest
.super java/lang/Object

.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static f(I)I
.limit stack 35
.limit locals 35
B1:
iload_0
iconst_1
iadd
istore 1
iload_1
iconst_2
iadd
istore 0
iload_0
iconst_3
iadd
istore 1
iload_1
iconst_1
iadd
istore 0
iload 0
ireturn
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 20
.limit locals 20
B1:
iconst_1
invokestatic registerTest/f(I)I
istore 2
iload_2
invokestatic io/println(I)V
return
.end method

