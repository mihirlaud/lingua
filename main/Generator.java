import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class Generator {

	private ArrayList<Token[]> tokens;
	private String rootFilename;
	private String name;
	private ArrayList<String> forLoops;

	public Generator(ArrayList<Token[]> tokens, String rootFilename) {
		this.tokens = tokens;
		this.rootFilename = rootFilename;
		if(rootFilename.indexOf("\\") != -1)
			this.name = rootFilename.substring(rootFilename.indexOf("\\") + 1, rootFilename.length() - 4);
		else
			this.name = rootFilename.substring(0, rootFilename.length() - 4);
		forLoops = new ArrayList<>();
	}
	
	public void generateJasminFile() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(rootFilename.substring(0, rootFilename.length() - 4) + ".j"));
			
			writer.write(".class public " + name);
			
			writer.append("\n.super java/lang/Object\n\n");
			
			ArrayList<String> imports = new ArrayList<>();
			ArrayList<String> variables = new ArrayList<>();
			ArrayList<String> functions = new ArrayList<>();
			
			int tabCount = 0;
			for(Token[] tline : tokens) {
				
				switch(tline[0 + tabCount].getType()) {
					case IMPORT:
						String filename = tline[1 + tabCount].getValue();
						Scanner sc;
						
						try {
							if(rootFilename.indexOf("\\") != -1) {
								filename = rootFilename.substring(0, rootFilename.indexOf("\\") + 1) + filename;
							}
							sc = new Scanner(new File(filename + ".j"));
							filename = filename + ".j";
						} catch(FileNotFoundException e) {
							try {
								sc = new Scanner(new File(filename + ".lng"));
								filename = filename + ".lng";
							} catch(FileNotFoundException err) {
								e.printStackTrace();
								return;
							}
						}
						
						boolean push = false;
						while(sc.hasNext()) {
							String f = sc.nextLine();
							if(f.contains(";;")) {
								push = true;
								f = sc.nextLine();
							}
							if(f.contains(";!")) {
								push = false;
							}
							if(f.contains(".method")) {
								functions.add(f.substring(22));
							}
							if(push)
								imports.add(f);
						}
						break;
					case DEFINE:
						if(tline.length == 4 + tabCount) {
							String type = tline[1 + tabCount].getType().toString();
							String name = tline[2 + tabCount].getValue();
							switch(type) {
								case "INT":
									type = "I";
									break;
								case "DEC":
									type = "D";
									break;
								case "BOOLEAN":
									type = "Z";
									break;
							}
							variables.add(name + " " + type);
						} else {
							tabCount++;
							break;
						}
						break;
					case IF:
						tabCount++;
						break;
					case FOR:
						String type = tline[2 + tabCount].getType().toString();
						String name = tline[3 + tabCount].getValue();
						switch(type) {
							case "INT":
								type = "I";
								break;
						}
						variables.add(name + " " + type);
						tabCount++;
						break;
					case WHILE:
						tabCount++;
						break;
					case ELSE:
						tabCount++;
						break;
					case ENDDEF:
						tabCount--;
						break;
					case ENDIF:
						tabCount--;
						break;
					case ENDFOR:
						tabCount--;
						break;
					case ENDWHILE:
						tabCount--;
						break;
				}
			}
			
			for(String var : variables)
				writer.append(".field public static " + var + "\n");
			
			for(String imp : imports)
				writer.append(imp + "\n");
			
			writer.append(".method public <init>()V\n");
			writer.append("\taload_0\n");
			writer.append("\tinvokenonvirtual java/lang/Object/<init>()V\n");
			writer.append("\treturn\n");
			writer.append(".end method\n\n");
			writer.append(".method public static main([Ljava/lang/String;)V\n");
			writer.append("\t.limit stack 5\n");
			writer.append("\t.limit locals 5\n");
			
			tabCount = 0;
			for(Token[] tline : tokens) {
				
				switch(tline[0 + tabCount].getType()) {
					case ASSIGN:
						Token[] expression = Arrays.copyOfRange(tline, 1 + tabCount, tline.length - 3);
						String target = tline[tline.length - 2].getValue();
						for(String var : variables) {
							if(var.indexOf(target + " ") == 0)
								target = var;
						}
						if(expression.length == 1) {
							if(expression[0].getType() == Terminal.Name) {
								for(String var : variables) {
									if(var.indexOf(expression[0].getValue() + " ") == 0) {
										writer.append("\tgetstatic " + this.name + "/" + var + "\n");
										writer.append("\tputstatic " + this.name + "/" + target + "\n");
										break;
									}
								}
							} else {
								writer.append("\tldc " + expression[0].getValue() + "\n");
								writer.append("\tputstatic " + this.name + "/" + target + "\n");
							}
						} else {
							String type = "";
							switch(expression[0].getType()) {
								case Name:
									for(String var : variables) {
										if(var.indexOf(expression[0].getValue() + " ") == 0)
											type = var.charAt(var.length() - 1) + "";
									}
									break;
								case Integer:
									type = "I";
									break;
								case Decimal:
									type = "D";
									break;
								case Boolean:
									type = "Z";
									break;	
							}
							ArrayList<Token> expList = new ArrayList<>(Arrays.asList(expression));
							ArrayList<String> instructions = new ArrayList<>();
							boolean first = true;
							if(type.equals("I")) {
								for(int i = 0; i < expList.size(); i++) {
									if(expList.get(i).getValue().equals("*") || expList.get(i).getValue().equals("/")) {
										if(first) {
											first = false;
											
											Token value1 = expList.get(i - 1);
											Token value2 = expList.get(i + 1);
											
											if(value1.getType() == Terminal.Name) {
												for(String var : variables) {
													if(var.indexOf(value1.getValue() + " ") == 0) {
														instructions.add("\tgetstatic " + this.name + "/" + var + "\n");
														break;
													}
												}
											} else {
												instructions.add("\tldc " + value1.getValue() + "\n");
											}
											
											if(value2.getType() == Terminal.Name) {
												for(String var : variables) {
													if(var.indexOf(value2.getValue() + " ") == 0) {
														instructions.add("\tgetstatic " + this.name + "/" + var + "\n");
														break;
													}
												}
											} else {
												instructions.add("\tldc " + value2.getValue() + "\n");
											}
											
											if(expList.get(i).getValue().equals("*"))
												instructions.add("\timul\n");
											else if(expList.get(i).getValue().equals("/"))
												instructions.add("\tidiv\n");
											instructions.add("\tistore_" + tabCount + "\n");
										} else {
											instructions.add("\tiload_" + tabCount + "\n");
											Token value2 = expList.get(i + 1);
											if(value2.getType() == Terminal.Name) {
												for(String var : variables) {
													if(var.indexOf(value2.getValue() + " ") == 0) {
														instructions.add("\tgetstatic " + this.name + "/" + var + "\n");
														break;
													}
												}
											} else {
												instructions.add("\tldc " + value2.getValue() + "\n");
											}
											
											if(expList.get(i).getValue().equals("*"))
												instructions.add("\timul\n");
											else if(expList.get(i).getValue().equals("/"))
												instructions.add("\tidiv\n");
											instructions.add("\tistore_" + tabCount + "\n");
										}
										
									}
								}
								first = true;
								for(int i = 0; i < expList.size(); i++) {
									if(expList.get(i).getValue().equals("+") || expList.get(i).getValue().equals("-")) {
										if(first) {
											first = false;
											
											Token value1 = expList.get(i - 1);
											Token value2 = expList.get(i + 1);
											
											if(value1.getType() == Terminal.Name) {
												for(String var : variables) {
													if(var.indexOf(value1.getValue() + " ") == 0) {
														instructions.add("\tgetstatic " + this.name + "/" + var + "\n");
														break;
													}
												}
											} else {
												instructions.add("\tldc " + value1.getValue() + "\n");
											}
											
											if(value2.getType() == Terminal.Name) {
												for(String var : variables) {
													if(var.indexOf(value2.getValue() + " ") == 0) {
														instructions.add("\tgetstatic " + this.name + "/" + var + "\n");
														break;
													}
												}
											} else {
												instructions.add("\tldc " + value2.getValue() + "\n");
											}
											
											if(expList.get(i).getValue().equals("+"))
												instructions.add("\tiadd\n");
											else if(expList.get(i).getValue().equals("-"))
												instructions.add("\tisub\n");
											instructions.add("\tistore_" + tabCount + "\n");
										} else {
											instructions.add("\tiload_" + tabCount + "\n");
											Token value2 = expList.get(i + 1);
											if(value2.getType() == Terminal.Name) {
												for(String var : variables) {
													if(var.indexOf(value2.getValue() + " ") == 0) {
														instructions.add("\tgetstatic " + this.name + "/" + var + "\n");
														break;
													}
												}
											} else {
												instructions.add("\tldc " + value2.getValue() + "\n");
											}
											
											if(expList.get(i).getValue().equals("+"))
												instructions.add("\tiadd\n");
											else if(expList.get(i).getValue().equals("-"))
												instructions.add("\tisub\n");
											instructions.add("\tistore_" + tabCount + "\n");
										}
										
									}
								}
							}
							for(String instruction : instructions)
								writer.append(instruction);
							writer.append("\tiload_" + tabCount + "\n");
							writer.append("\tputstatic " + this.name + "/" + target + "\n");
						}
						break;
					case CALL:
						String tempFunc = "";
						ArrayList<Token> params = new ArrayList<>();
						boolean name = true;
						boolean next = false;
						for(Token t : Arrays.copyOfRange(tline, 1 + tabCount, tline.length - 1)) {
							if(t.getType() == Terminal.OpenParen)
								name = false;
							if(name) {
								if(t.getType() == Terminal.Colon)
									tempFunc += "_";
								else
									tempFunc += t.getValue();
							} else {
								if(next) {
									next = false;
									params.add(t);
								}
								if(t.getType() == Terminal.Colon)
									next = true;
							}
						}
						for(Token param : params) {
							if(param.getType() == Terminal.Name) {
								for(String var : variables) {
									if(var.indexOf(param.getValue() + " ") == 0)
										writer.append("\tgetstatic " + this.name + "/" + var + "\n");
								}
							} else {
								
							}
						}
						for(String func : functions) {
							if(func.indexOf(tempFunc) == 0) {
								writer.append("\tinvokestatic " + this.name + "/" + func + "\n");
								break;
							}
						}
						break;
					case IF:
						tabCount++;
						break;
					case FOR:
						String fortarget = tline[3 + tabCount].getValue();
						for(String var : variables) {
							if(var.indexOf(fortarget + " ") == 0) {
								fortarget = var;
								break;
							}
						}
						if(tline[5 + tabCount].getType() == Terminal.Name) {
							for(String var : variables) {
								if(var.indexOf(tline[5 + tabCount].getValue() + " ") == 0) {
									writer.append("\tgetstatic " + this.name + "/" + var + "\n");
									break;
								}
							}
						} else {
							writer.append("\tldc " + tline[5 + tabCount].getValue() + "\n");
						}
						writer.append("\tputstatic " + this.name + "/" + fortarget + "\n");
						
						if(tline[7 + tabCount].getType() == Terminal.Name) {
							for(String var : variables) {
								if(var.indexOf(tline[7 + tabCount].getValue() + " ") == 0) {
									writer.append("\tgetstatic " + this.name + "/" + var + "\n");
									break;
								}
							}
						} else {
							writer.append("\tldc " + tline[7 + tabCount].getValue() + "\n");
						}
						writer.append("\tistore_" + tabCount + "\n");
						
						forLoops.add(fortarget);
						
						writer.append("\t#_" + tabCount + ":\n");
						tabCount++;
						break;
					case WHILE:
						tabCount++;
						break;
					case ELSE:
						tabCount++;
						break;
					case ENDDEF:
						tabCount--;
						break;
					case ENDIF:
						tabCount--;
						break;
					case ENDFOR:
						tabCount--;
						String correspondingForTarget = forLoops.get(tabCount);
						writer.append("\tgetstatic " + this.name + "/" + correspondingForTarget + "\n");
						writer.append("\tldc 1\n");
						writer.append("\tiadd\n");
						writer.append("\tputstatic " + this.name + "/" + correspondingForTarget + "\n");
						writer.append("\tgetstatic " + this.name + "/" + correspondingForTarget + "\n");
						writer.append("\tiload_" + tabCount + "\n");
						writer.append("\tisub\n");
						writer.append("\tiflt #_" + tabCount + "\n");
						break;
					case ENDWHILE:
						tabCount--;
						break;
				}
			}
			
			writer.append("\treturn\n");
			writer.append(".end method");
			writer.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void executeJasminFile() {
		
	}

}