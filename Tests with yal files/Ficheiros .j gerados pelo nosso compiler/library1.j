.class public library1
.super java/lang/Object

.method public <init>()V
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method

.method public static max(II)I
.limit stack 25
.limit locals 25
B1:
block0:
iload_0
iload_1
if_icmpgt block0true
block2:
iload_1
istore 2
goto block3
block0true:
block1:
iload_0
istore 2
block3:
iload 2
ireturn
.end method

.method public static min(II)I
.limit stack 25
.limit locals 25
B1:
block4:
iload_0
iload_1
if_icmpgt block4true
block6:
iload_0
istore 2
goto block7
block4true:
block5:
iload_1
istore 2
block7:
iload 2
ireturn
.end method

