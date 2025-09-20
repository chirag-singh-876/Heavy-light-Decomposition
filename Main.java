import java.util.*;
 
import javax.annotation.processing.SupportedSourceVersion;
 
import java.io.*;
// import java.lang.classfile.constantpool.IntegerEntry;
// import java.lang.reflect.Array;
 
public class Main {
    static class Data implements Comparable<Data>{
        long ai;
        long bi;
        long ci;
        public Data(long ai, long bi, long ci){
            this.ai = ai;
            this.bi = bi;
            this.ci = ci;
        }
        @Override
        public int compareTo(Data p1){
            return (int)(this.bi-p1.bi);
        }
    }
    // static class Data2 implements Comparable<Data>{
    //     int ai;
    //     int bi;
    //     int ci;
    //     public Data2(int ai, int bi, int ci){
    //         this.ai = ai;
    //         this.bi = bi;
    //         this.ci = ci;
    //     }
    //     @Override
    //     public int compareTo(Data p1){
    //         return (this.bi-p1.bi);
    //     }
    // }
    static class Node{
        int a;
        int b;
        public Node(int a, int b){
            this.a = a;
            this.b = b;
        }
    }
    // static class Edge {
    //     int dst;
    //     int dir;
    //     public Edge(int dst,int dir){
    //         this.dir = dir;
    //         this.dst = dst;
    //     }
    // }
    
    public static PP.Reader cs = new PP.Reader(System.in);
    // public static long ans[];
    // public static BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));
    // public static long[][] ncr = new long[51][51];
    public static void main(String[] args) throws IOException{
        int tests = 1;//cs.nextInt();
        while(tests>0){
            tests--;
            solve();
        }
        // writer.flush();
        // writer.close();
    }
    public static long mod = 1000000000 + 7;
    public static long MOD = 998244353; // type it
 
    // public static long value_imp[];
    // public static int range[][];
    public static long[] val ; // Stores the value assigned to node
    public static int[] depth; 
    public static int[] parent;
    public static int[] heavynode; // stores which child is heavy child. 0 for leaf node.
    public static int[] size;   // Stores the subtree size.
    public static long[] lt;  // Main Segment array,  every segment is continuous on it. it stores values of node 
    public static int[] assidx; // Stores the index assigned to a node in lt array
    public static int[] head; // Stores the chain number a node.. Chain Number of node is the node from where the chain started.
    public static int idx = 0; // just an iterator
    public static void solve() throws IOException{
        /*  This paritcular problem is finding Max value on node paths. 
            Problem is Path Queries II on cses. Link :- https://cses.fi/problemset/result/14604401/        
        */
        int n = cs.nextInt();
        int q = cs.nextInt();
        val = new long[n+1];
        for(int i=1;i<=n;i++) val[i] = cs.nextLong();
        ArrayList<ArrayList<Integer>> tree = new ArrayList<>();
        for(int i=0;i<=n;i++) tree.add(new ArrayList<>());
        for(int i=1;i<n;i++){
            int u = cs.nextInt();
            int v = cs.nextInt();
            tree.get(u).add(v);
            tree.get(v).add(u);
        }
        parent = new int[n+1];
        depth = new int[n+1];
        size = new int[n+1];
        heavynode = new int[n+1];
        dfs(tree, 1, 0);
 
        head = new int[n+1];
        idx = 1;
        assidx = new int[n+1];
        lt = new long[n+1];
        dfshld(tree, 1,0, 1);
 
        SegmentTree sg = new SegmentTree(lt);
        while (q>0) { 
            q--;
            int qtype = cs.nextInt();
            if(qtype ==1){
                int idx = assidx[cs.nextInt()];
                sg.update(idx, cs.nextLong());
                continue;
            }
            int a = cs.nextInt();
            int b = cs.nextInt();
            long ans = 0;
            while (head[a] != head[b]) {
                if(depth[head[a]]<depth[head[b]]){
                    int t=a;
                        a =b;
                        b = t;
                }
                long query = sg.rangeMax(assidx[head[a]],assidx[a]);
                ans = Math.max(ans,query);
                a = parent[head[a]];
            }
            if(depth[a]<depth[b]){
                int t=a;
                    a =b;
                    b = t;
            }
            long query = sg.rangeMax(assidx[b], assidx[a]);
            ans = Math.max(ans,query);
            System.out.print(ans+" ");
        }
        
 
 
    }
    //Write fxn here
    public static void dfs(ArrayList<ArrayList<Integer>> tree, int curr,int par){
        parent[curr] = par;
        depth[curr] = depth[par]+1;
        for(Integer nb: tree.get(curr)){
            if(nb == par) continue;
            dfs(tree, nb, curr);
            size[curr] += size[nb];
            if(size[nb]>size[heavynode[curr]]){
                heavynode[curr] = nb;
            }
        }
        size[curr]++;
 
    }
 
    public static void dfshld(ArrayList<ArrayList<Integer>> tree, int curr, int par,int chain){
        head[curr] = chain;
        lt[idx] = val[curr];
        assidx[curr] = idx;
        idx++;
        if(heavynode[curr] != 0) dfshld(tree, heavynode[curr], curr, chain);
        for(Integer nb:tree.get(curr) ){
            if(nb == par || nb == heavynode[curr])continue;
            else dfshld(tree, nb, curr, nb);
        }
 
    }
    public static int kth_ancestor(int[][] binpar,int node,int k){
        for(int i=0;i<21;i++){
            int bitset = (k>>i)&1;
            if(bitset ==1){
                node = binpar[i][node];
            }
            if(node == -1) return -2;
        }
        return node;
    }
    
    public static long gcd(long a,long b){
        // System.out.println(a+" "+b);
        long c = Math.min(a,b);
        long d = Math.max(a,b);
        if(d%c==0) return c;
        return gcd(c,d%c);
    }
    public static long divmod(long a,long b,long m){   //claculate (a/b)%m
        a = a%m;b = b%m;  // Include if not working
        long inv =  powermod(b, m-2, m);
        return (inv*a)%m;
    }
    static long powermod(long x, long y, long p) //calculate (x^y)%p
    {
        long res = 1;
        x = x % p; 
        while (y > 0) {
            if ((y & 1) > 0)
                res = (res * x) % p;
            y = y >> 1; 
            x = (x * x) % p;
        }
        return res;
    }
}
 
class Fenwick{
    /* 
    Note this works on 1 based indexing;
    update(2,4) will do arr[1] += 4;
    predixsum(2) gives  sum of arr[0]+arr[1];
    rangesum(3,5) gives sum of arr[2]+arr[3]+arr[4];
    */
 
    public static long mod = 1000000000 + 7;
    long fenarr[];
    public Fenwick(int size){
        fenarr = new long[size+1];
    }
    public Fenwick(long arr[]){
        fenarr = new long[arr.length+1];
        for(int i=1;i<=arr.length;i++) update(i, arr[i-1]);
    }
    public void update(int idx,long change){
        while(idx<fenarr.length){
            fenarr[idx] = (fenarr[idx] + change)%mod;
            idx += (idx&-idx);
        }
    }
    // Prefix sum query: sum of [1..idx]
    public long prefixSum(int idx) {
        long ans = 0;
        while (idx > 0) {
            ans = (ans + fenarr[idx]) % mod;
            idx -= (idx & -idx);
        }
        return ans;
    }
 
    // Range sum query: sum of [l..r], 1-based
    public long rangeSum(int l, int r) {
        if (l > r) return 0;
        return (prefixSum(r) - prefixSum(l - 1) + mod) % mod;
    }
}
 
class PP {
    static String IN = "%s";
    static String OUT = "%s";
 
    static class Reader {
        BufferedReader br;
        StringTokenizer st;
 
        Reader(InputStream is) {
            br = new BufferedReader(new InputStreamReader(is));
        }
 
        String next() {
            try {
                while (st == null || !st.hasMoreTokens())
                    st = new StringTokenizer(br.readLine());
                return st.nextToken();
            } catch (Exception ignored) {
            }
            return null;
        }
 
        int nextInt() {
            return Integer.parseInt(next());
        }
 
        long nextLong() {
            return Long.parseLong(next());
        }
 
        double nextDouble() {
            return Double.parseDouble(next());
        }
    }
} 
class SegmentTree {
    private final int n;
    private final long[] tree;   // segment tree array (stores max)
    private final long[] arr;    // original values
 
    public SegmentTree(long[] input) {
        this.n = input.length;
        this.arr = new long[n];
        System.arraycopy(input, 0, this.arr, 0, n);
        this.tree = new long[4 * Math.max(1, n)];
        if (n > 0) build(1, 0, n - 1);
    }
 
    // Build tree node 'node' covering segment [l, r]
    private void build(int node, int l, int r) {
        if (l == r) {
            tree[node] = arr[l];
            return;
        }
        int mid = l + (r - l) / 2;
        build(node << 1, l, mid);
        build(node << 1 | 1, mid + 1, r);
        tree[node] = Math.max(tree[node << 1], tree[node << 1 | 1]); // take max instead of sum
    }
 
    /** Public API: range max query for [l, r] inclusive (0-based). */
    public long rangeMax(int l, int r) {
        // System.out.println(l+" -- "+r);
        if (l < 0 || r >= n || l > r) throw new IllegalArgumentException("Invalid query range");
        return query(1, 0, n - 1, l, r);
    }
 
    // recursive query on node covering [nl, nr], asking for [ql, qr]
    private long query(int node, int nl, int nr, int ql, int qr) {
        if (ql > nr || qr < nl) return Long.MIN_VALUE; // no overlap → return lowest value
        if (ql <= nl && nr <= qr) return tree[node];   // total overlap
        int mid = nl + (nr - nl) / 2;
        long left = query(node << 1, nl, mid, ql, qr);
        long right = query(node << 1 | 1, mid + 1, nr, ql, qr);
        return Math.max(left, right); // take maximum
    }
 
    /** Public API: point update index idx to newVal (0-based). */
    public void update(int idx, long newVal) {
        if (idx < 0 || idx >= n) throw new IllegalArgumentException("Index out of bounds");
        updateNode(1, 0, n - 1, idx, newVal);
        arr[idx] = newVal;
    }
 
    // recursive point update at position pos within node range [nl, nr]
    private void updateNode(int node, int nl, int nr, int pos, long newVal) {
        if (nl == nr) {
            tree[node] = newVal;
            return;
        }
        int mid = nl + (nr - nl) / 2;
        if (pos <= mid) updateNode(node << 1, nl, mid, pos, newVal);
        else updateNode(node << 1 | 1, mid + 1, nr, pos, newVal);
        tree[node] = Math.max(tree[node << 1], tree[node << 1 | 1]); // update max
    }
 
    /** Optional: returns original array value at idx. */
    public long get(int idx) {
        if (idx < 0 || idx >= n) throw new IllegalArgumentException("Index out of bounds");
        return arr[idx];
    }
}