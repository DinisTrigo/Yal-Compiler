.class public max_array
.super java/lang/Object

.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static maxarray([I)I
.limit stack 25
.limit locals 25
B1:
aload_0
iconst_0
iaload
istore 1
iconst_1
istore 2
block0loop:
iload_2
aload_0
arraylength
if_icmplt block0branch
goto block4
block0branch:
block1:
iload_1
aload_0
iload_2
iaload
if_icmplt block1true
goto block3
block1true:
block2:
aload_0
iload_2
iaload
istore 1
block3:
iload_2
iconst_1
iadd
istore 2
iload_2
aload_0
arraylength
if_icmplt block0branch
block4:
ldc "max: "
iload_1
invokestatic io/print(Ljava/lang/String;I)V
iload 1
ireturn
.end method

.method public static main([Ljava/lang/String;)V
.limit stack 30
.limit locals 30
B1:
bipush 10
newarray int
astore 3
iconst_0
istore 4
block5loop:
iload 4
bipush 10
if_icmplt block5branch
goto block7
block5branch:
block6:
iload 4
istore 4
aload 3
iload 4
iload 4
iastore
iload 4
iconst_1
iadd
istore 4
iload 4
bipush 10
if_icmplt block5branch
block7:
aload_3
invokestatic max_array/maxarray([I)I
istore 1
return
.end method

