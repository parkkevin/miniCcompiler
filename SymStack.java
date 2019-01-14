package x86;

	public class SymStack {

		SymTab ss[];
		int size;
		int[] next;
		int[] prev;
		int tos;

		int temps;
		int offset;





		public SymStack () {
			ss = new SymTab [1000];
			next = new int [1000];
			prev = new int [1000];

			offset = 0;
			ss[0] = new SymTab (offset);
			size = 1;
			next[0] = -1;
			prev[0] = -1;
			tos = 0;

			temps = 0;



		}

		void IncOffset () {
			offset = (((offset + 8) + 15) / 16) * 16;
		}

		public void FunctionEntry() {
			offset = 0;
			PushSymTab();

		}

		public void BlockEntry() {
			PushSymTab();
		}



		void PushSymTab () {

			ss[size] = new SymTab (offset);
			next[size] = -1;
			prev[size] = tos;
			next[tos] = size;

			tos = size;
			size ++;
		}

		public void PopSymTab (QuadTab q) {



			tos = prev[tos];
			next[tos] = -1;



		}

		public Symbol Find (String n) {
			int id = tos;
			while (id != -1) {
				Symbol s = ss[id].Find(n);
				if (s != null) return s;
				id = prev[id];
			}
			return null;
		}

		public Symbol insert(String n, DataType d) {
			Symbol id = Find(n);
			if (id != null) return id;

			Symbol ns = Add(n,d);
			return ns;
		}

		public Symbol insertString(String n, DataType d) {
			Symbol id = Find(n);
			if (id != null) return id;

			Symbol ns = AddString(n,d);
			return ns;
		}

		public Symbol insert(String n, DataType d, Boolean isConstant) {
			Symbol id = Find(n);
			if (id != null) return id;

			Symbol ns = Add(n, d, isConstant);
			return ns;

		}

		public Symbol Add (String n, DataType d) {
			Symbol ns = ss[tos].Add(n, d, offset);
			IncOffset();
			return ns;
		}

		public Symbol AddString (String n, DataType d) {
			Symbol ns = ss[0].Add(n, d, offset);
			IncOffset();
			return ns;
		}

		public Symbol Add (String n, DataType d, Symbol val) {
			Symbol ns = ss[tos].Add(n, d, val, offset);
			IncOffset();
			return ns;
		}

		public Symbol Add (String n, DataType d, Boolean isConstant) {
			Symbol ns = ss[tos].Add(n, d, isConstant, offset);
			IncOffset();
			return ns;
		}

		public Symbol Add (String n, DataType d, int sz) {
			Symbol ns = ss[tos].Add(n, d, sz, offset);
			IncOffset();
			return ns;
		}

		public Symbol Add (int n) {
			Symbol id = Find("L_" + n);
			if (id != null) return id;

			Symbol ns = ss[0].Add(n, DataType.LABEL, Boolean.TRUE, offset);
			IncOffset();
			return ns;
		}

		public Symbol Add (DataType d) {
			Symbol ns = ss[tos].Add(temps++, d, offset);
			IncOffset();
			return ns;
		}

		public void Print () {
			for (int i = 0; i < size; i++) {
				System.out.println("#-------------------------------------");
				ss[i].Print();
				System.out.println("#-------------------------------------");
			}
		}

		public void PrintGlobals() {
			ss[0].PrintGlobals();
		}


		public int GetOffset() {
			return offset;
		}

	}
