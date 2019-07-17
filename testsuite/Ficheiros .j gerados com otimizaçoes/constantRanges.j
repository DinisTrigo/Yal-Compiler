.class public constantRanges
.super java/lang/Object

.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static f()V
.limit stack 45
.limit locals 45
B1:
iconst_5
istore 0
iconst_m1
istore 1
bipush 6
istore 2
sipush 128
istore 3
sipush -129
istore 4
ldc 32768
istore 5
ldc -32769
istore 6
iconst_5
invokestatic io/println(I)V
iconst_m1
invokestatic io/println(I)V
bipush 6
invokestatic io/println(I)V
sipush 128
invokestatic io/println(I)V
sipush -129
invokestatic io/println(I)V
ldc 32768
invokestatic io/println(I)V
ldc -32769
invokestatic io/println(I)V
return
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 15
.limit locals 15
B1:
invokestatic constantRanges/f()V
return
.end method

