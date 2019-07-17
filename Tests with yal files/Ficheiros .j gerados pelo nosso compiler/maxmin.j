.class public maxmin
.super java/lang/Object

.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static maxmin()I
.limit stack 15
.limit locals 15
B1:
invokestatic io/read()I
istore 0
block0:
iload_0
iconst_0
if_icmplt block0true
block4loop:
iload_0
iconst_0
if_icmpgt block4branch
goto block6
block4branch:
block5:
iload_0
iconst_1
isub
istore 0
iload_0
iconst_0
if_icmpgt block4branch
block6:
goto block7
block0true:
block1loop:
iload_0
iconst_0
if_icmplt block1branch
goto block3
block1branch:
block2:
iload_0
iconst_1
iadd
istore 0
iload_0
iconst_0
if_icmplt block1branch
block3:
block7:
ldc "a"
iload_0
invokestatic io/println(Ljava/lang/String;I)V
iload 0
ireturn
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 20
.limit locals 20
B1:
invokestatic maxmin/maxmin()I
istore 2
ldc "a="
iload_2
invokestatic io/println(Ljava/lang/String;I)V
return
.end method

