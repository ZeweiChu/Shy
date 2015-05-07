package library;

public class Pair<A, B> {
	private A a;
	private B b;
	public Pair(A a, B b) { this.a = a; this.b = b; }
	public A a() { return a; }
	public B b() { return b; }
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Pair)) {
			return false;
		}
		Pair<A, B> p = (Pair<A,B>)obj;
		return a.equals(p.a()) && b.equals(p.b());
	}
	
	@Override
	public int hashCode() {
		return a.hashCode() + b.hashCode();
	}
	
	@Override
	public String toString() {
		return "<" + a + ", " + b + ">";
	}
}
