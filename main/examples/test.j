.class public test
.super java/lang/Object

.field public static a Z

.method public static stdio__print_int(I)V
	.limit stack 2
	.limit locals 1
	
	getstatic java/lang/System/out Ljava/io/PrintStream;
	iload 0
	invokevirtual java/io/PrintStream/println(I)V
	
	return
.end method


.method public static stdio__print_bool(Z)V
	.limit stack 2
	.limit locals 1
	
	getstatic java/lang/System/out Ljava/io/PrintStream;
	iload 0
	invokevirtual java/io/PrintStream/println(Z)V
	
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
	return
.end method