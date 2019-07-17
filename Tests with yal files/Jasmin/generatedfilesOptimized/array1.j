.class public array1
.super java/lang/Object

.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static print_array(I)V
.limit stack 30
.limit locals 30
B1:
iload_0
newarray int
astore 1
iconst_0
istore 3
block0loop:
iload_3
iload_0
if_icmplt block0branch
goto block2
block0branch:
block1:
iload_3
istore 4
aload 1
iload_3
iload 4
iastore
iload_3
iconst_1
iadd
istore 3
iload_3
iload_0
if_icmplt block0branch
block2:
iconst_0
istore 3
block3loop:
iload_3
iload_0
if_icmplt block3branch
goto block5
block3branch:
block4:
aload_1
iload_3
iaload
istore 2
ldc "a: "
aload_1
iload_3
iaload
invokestatic io/print(Ljava/lang/String;I)V
iload_3
iconst_1
iadd
istore 3
iload_3
iload_0
if_icmplt block3branch
block5:
return
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 15
.limit locals 15
B1:
bipush 10
invokestatic array1/print_array(I)V
return
.end method

