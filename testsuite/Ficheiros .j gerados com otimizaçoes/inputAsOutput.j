.class public inputAsOutput
.super java/lang/Object

.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static f(I)I
.limit stack 15
.limit locals 15
B1:
iload 0
ireturn
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 20
.limit locals 20
B1:
iconst_1
invokestatic inputAsOutput/f(I)I
istore 2
iload_2
invokestatic io/println(I)V
return
.end method

