.class public array2
.super java/lang/Object

.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static sum_array([I)I
.limit stack 25
.limit locals 25
B1:
iconst_0
istore 1
iconst_0
istore 2
block0loop:
iload_1
aload_0
arraylength
if_icmplt block0branch
goto block2
block0branch:
block1:
iload_2
aload_0
iload_1
iaload
iadd
istore 2
iload_1
iconst_1
iadd
istore 1
iload_1
aload_0
arraylength
if_icmplt block0branch
block2:
iload 2
ireturn
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 35
.limit locals 35
B1:
bipush 16
istore 4
iload 4
newarray int
astore 2
iconst_0
istore 3
block3loop:
iload_3
iload 4
if_icmplt block3branch
goto block5
block3branch:
block4:
iconst_1
istore 5
aload 2
iload_3
iload 5
iastore
iload_3
iconst_1
iadd
istore 3
iload_3
iload 4
if_icmplt block3branch
block5:
aload_2
invokestatic array2/sum_array([I)I
istore 3
ldc "sum of array elements = "
iload_3
invokestatic io/println(Ljava/lang/String;I)V
return
.end method

