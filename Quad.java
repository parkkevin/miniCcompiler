package x86;

	public class Quad {

		Symbol label;
		String op;
		Symbol src1;
		Symbol src2;
		Symbol dst;


		public Quad (SymStack s, int l, Symbol d, Symbol s1, Symbol s2, String o) {
			label = s.Add(l);
			dst = d;
			src1 = s1;
			src2 = s2;
			op = o;
		}

		public Quad (Symbol l) {
			label = l;
			dst = null;
			src1 = null;
			src2 = null;
			op = "";
		}

		public void BackPatch (Symbol l) {
			dst = l;
		}

		public Symbol GetLabel () {
			return label;
		}

		public void Print () {
			System.out.print(label.GetName() + ": ");
			if (dst != null) System.out.print(dst.GetName());
			if (src1 != null) System.out.print(" = " + src1.GetName());
			System.out.print(" " + op + " ");
			if (src2 != null) System.out.print(src2.GetName());
			System.out.println("");
		}

		public void AsmPrint () {

			System.out.print(label.GetName() + ": ");


			if (op.equals("")) {
				System.out.println("push %rbp");
				System.out.println("movq %rsp, %rbp");
			} else if (op.equals("frame")) {
				System.out.println("subq " + dst.AsmPrint() + ", %rsp");
			} else if (op.equals("ret")) {
				if (src1 != null) System.out.println("movq -" + src1.GetOffset() + "(%rbp), %rax");
				System.out.println("addq $" + dst.GetName() + ", %rsp");
				System.out.println("pop %rbp");
				System.out.println("ret");
			} else if (op.equals("=")) {
				ReadSrc1(src1);
				WriteDst(dst);
			} else if (op.equals("*")) {
				ReadSrc1(src1);
				ReadSrc2(src2);
				Compute("imul");
				WriteDst(dst);
			} else if (op.equals("/")) {
				ReadSrc1(src1);
				ReadSrc2(src2);
				Compute("idiv");
				WriteDst(dst);
			} else if (op.equals("+")) {
				ReadSrc1(src1);
				ReadSrc2(src2);
				Compute("addq");
				WriteDst(dst);
			} else if (op.equals("-")) {
				ReadSrc1(src1);
				ReadSrc2(src2);
				Compute("subq");
				WriteDst(dst);
			} else if (dst == null && src1 == null && src2 == null) {
				System.out.println(op);
			} else if (op.equals("if")) {
					String str = dst.AsmPrint();
					System.out.println("je " + str.substring(1, str.length()));
			} else if (op.equals("cmp")) {
				ReadSrc1(src1);
				ReadSrc2(src2);
				System.out.println("cmp %rax, %rbx");
			} else if (op.equals("jg") || op.equals("jl") || op.equals("jge") || op.equals("jle") || op.equals("je") || op.equals("jne")) {
				System.out.print(op + " ");
				if (dst != null) {
					String str = dst.AsmPrint();
					System.out.println(str.substring(1, str.length()));
				}
			} else if ((op.equals("rdi") || op.equals("rsi") || op.equals("rdx") || op.equals("rcx") || op.equals("r8") || op.equals("r9"))) {
				if (dst.GetType().equals(DataType.STR)){
					System.out.println("movq $str" + dst.GetOffset() + ", %" + op);
				} else if (src1 != null) {
					System.out.println("movq %" + op + ", " + dst.AsmPrint());
				} else {
					if (op.equals("rdi"))
						WriteFuncParam(dst, "rdi");
					else if (op.equals("rsi"))
						WriteFuncParam(dst, "rsi");
					else if (op.equals("rdx"))
						WriteFuncParam(dst, "rdx");
					else if (op.equals("rcx"))
						WriteFuncParam(dst, "rcx");
					else if (op.equals("r8"))
						WriteFuncParam(dst, "r8");
					else if (op.equals("r9"))
						WriteFuncParam(dst, "r9");
				}
			} else if (op.equals("call") || op.equals("callexp")) {
				System.out.println("call " + src1.GetName());
				if (dst != null) {
					System.out.println("movq %rax, " + dst.AsmPrint());
				}
			} else if (op.equals("goto")) {
					String str = dst.AsmPrint();
					System.out.println("jmp " + str.substring(1, str.length()));
			} else if (op.equals("[]")) {
					System.out.println("movq " + src1.AsmPrint() + ", %rax");
					System.out.println("movq " + src2.AsmPrint() + ", %rbx");
					System.out.println("addq %rbx, %rax");
					System.out.println("movq (%rax), %rbx");
					System.out.println("movq %rbx, " + dst.AsmPrint());
			} else if (op.equals("[]=")) {
					ReadSrc1(src1);
					ReadSrc2(src2);
					System.out.println("movq " + dst.AsmPrint() + ", %r10");
					System.out.println("addq %r10, %rax");
					System.out.println("movq %rbx, (%rax)");
			}
		}

		void Compute (String opcode) {
			System.out.println(opcode + " %rbx, %rax");
		}

		void ReadSrc1 (Symbol src) {
			System.out.println("movq " + src.AsmPrint() + ", %rax");
		}
;
		void ReadSrc2 (Symbol src) {
			System.out.println("movq " + src.AsmPrint() + ", %rbx");
		}

		void WriteDst (Symbol dst) {
			System.out.println("movq %rax, " + dst.AsmPrint());
		}

		void WriteFuncParam(Symbol dst, String reg) {
			System.out.println("mov " + dst.AsmPrint() + ", %" + reg);
		}



	}
