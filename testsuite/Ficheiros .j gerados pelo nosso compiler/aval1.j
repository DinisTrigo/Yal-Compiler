.class public aval1
.super java/lang/Object

.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 20
.limit locals 20
B1:
iconst_2
iconst_3
invokestatic aval1/f(II)I
istore 2
iload_2
invokestatic io/println(I)V
return
.end method

.method public static f(II)I
.limit stack 25
.limit locals 25
B1:
iload_0
iload_1
imul
istore 2
iload 2
ireturn
.end method

