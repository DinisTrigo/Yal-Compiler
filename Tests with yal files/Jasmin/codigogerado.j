.class public array1
.super java/lang/Object
.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static print_array(I)V
.limit stack 9999
.limit locals 9999
B1:
iload 0
newarray int
astore 1
ldc 0
istore 2
block0loop:
iload 2
iload 0
if_icmplt block0branch
goto block2
block0branch:
block1:
iload 2
istore 3
aload 1
iload 2
iload 3
iastore
iload 2
ldc 1
iadd
istore 2
goto block0loop
block2:
ldc 0
istore 2
block3loop:
iload 2
iload 0
if_icmplt block3branch
goto block5
block3branch:
block4:
aload 1
iload 2
iaload
istore 3
ldc "a: "
iload 3
invokestatic io/print(Ljava/lang/String;I)V
iload 2
ldc 1
iadd
istore 2
goto block3loop
block5:
return
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 9999
.limit locals 9999
B1:
ldc 10
invokestatic array1/print_array(I)V
return
.end method

