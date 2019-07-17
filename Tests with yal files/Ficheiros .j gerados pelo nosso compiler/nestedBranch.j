.class public nestedBranch
.super java/lang/Object

.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static sign(I)I
.limit stack 20
.limit locals 20
B1:
block0:
iload_0
iconst_0
if_icmplt block0true
block2:
iload_0
iconst_0
if_icmpeq block2true
block4:
iconst_1
istore 1
goto block5
block2true:
block3:
iconst_0
istore 1
block5:
goto block6
block0true:
block1:
iconst_m1
istore 1
block6:
iload 1
ireturn
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 45
.limit locals 45
B1:
bipush -10
istore 1
bipush 10
istore 4
iload_1
iload 4
iadd
istore 3
iload_1
invokestatic nestedBranch/sign(I)I
istore 5
iload_3
invokestatic nestedBranch/sign(I)I
istore 1
iload 4
invokestatic nestedBranch/sign(I)I
istore 3
iload 5
invokestatic io/println(I)V
iload_1
invokestatic io/println(I)V
iload_3
invokestatic io/println(I)V
return
.end method

