.class public max1
.super java/lang/Object

.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static max()I
.limit stack 40
.limit locals 40
B1:
invokestatic io/read()I
istore 0
invokestatic io/read()I
istore 1
invokestatic io/read()I
istore 2
invokestatic io/read()I
istore 3
invokestatic io/read()I
istore 4
iload_0
istore 5
block0:
iload 5
iload_1
if_icmplt block0true
goto block2
block0true:
block1:
iload_1
istore 5
block2:
block3:
iload 5
iload_2
if_icmplt block3true
goto block5
block3true:
block4:
iload_2
istore 5
block5:
block6:
iload 5
iload_3
if_icmplt block6true
goto block8
block6true:
block7:
iload_3
istore 5
block8:
block9:
iload 5
iload 4
if_icmplt block9true
goto block11
block9true:
block10:
iload 4
istore 5
block11:
ldc "max "
iload 5
invokestatic io/print(Ljava/lang/String;I)V
iload 5
ireturn
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 20
.limit locals 20
B1:
invokestatic max1/max()I
istore 1
return
.end method

