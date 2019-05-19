.class public fibonacci
.super java/lang/Object

.field public static a I
.field public static b I
.field public static x I

.method public static stdio__print_int(I)V
	.limit stack 2
	.limit locals 1
	
	getstatic java/lang/System/out Ljava/io/PrintStream;
	iload 0
	invokevirtual java/io/PrintStream/println(I)V
	
	return
.end method

.method public <init>()V
	aload_0
	invokenonvirtual java/lang/Object/<init>()V
	return
.end method

.method public static main([Ljava/lang/String;)V
	.limit stack 5
	.limit locals 5
	ldc 1
	putstatic fibonacci/a I
	ldc 1
	putstatic fibonacci/b I
	ldc 0
	putstatic fibonacci/x I
	ldc 10
	istore_0
	#_0:
	getstatic fibonacci/a I
	invokestatic fibonacci/stdio__print_int(I)V
	getstatic fibonacci/a I
	getstatic fibonacci/b I
	iadd
	istore_1
	iload_1
	putstatic fibonacci/a I
	getstatic fibonacci/a I
	getstatic fibonacci/b I
	isub
	istore_1
	iload_1
	putstatic fibonacci/b I
	getstatic fibonacci/x I
	ldc 1
	iadd
	putstatic fibonacci/x I
	getstatic fibonacci/x I
	iload_0
	isub
	iflt #_0
	return
.end method