#include <bits/stdc++.h>
#include <charconv>
using namespace std;

#define FAST_IO ios::sync_with_stdio(false); cin.tie(nullptr);

long long mod = 1000000000 + 7;
long long MOD = 998244353;

vector<long long> val;
vector<int> depthv, parentv, heavynode, sizeArr, assidx, headv;
vector<long long> lt;
int idxGlob = 0;

struct SegIter {
    int n;
    int base;
    vector<long long> seg;
    SegIter() : n(0), base(1) {}
    SegIter(int _n) { init(_n); }
    void init(int _n){
        n = _n;
        base = 1;
        while(base < n) base <<= 1;
        seg.assign(2*base, LLONG_MIN);
    }
    void build(const vector<long long> &arr){ // arr is 1-based length n+1
        for(int i=1;i<=n;i++) seg[base + i - 1] = arr[i];
        for(int i=base-1;i>=1;i--) seg[i] = max(seg[i<<1], seg[i<<1|1]);
    }
    void update(int idx, long long v){
        int p = base + idx - 1;
        seg[p] = v;
        p >>= 1;
        while(p){
            seg[p] = max(seg[p<<1], seg[p<<1|1]);
            p >>= 1;
        }
    }
    long long rangeMax(int l, int r){
        if(l>r) return LLONG_MIN;
        int L = base + l - 1, R = base + r - 1;
        long long res = LLONG_MIN;
        while(L <= R){
            if(L & 1) res = max(res, seg[L++]);
            if(!(R & 1)) res = max(res, seg[R--]);
            L >>= 1; R >>= 1;
        }
        return res;
    }
};

void dfs_iter(const vector<vector<int>> &tree, int root){
    vector<int> st;
    vector<int> order;
    st.push_back(root);
    parentv[root] = 0;
    depthv[root] = 0;
    while(!st.empty()){
        int node = st.back(); st.pop_back();
        order.push_back(node);
        for(int nb : tree[node]){
            if(nb == parentv[node]) continue;
            parentv[nb] = node;
            depthv[nb] = depthv[node] + 1;
            st.push_back(nb);
        }
    }
    for(int i=(int)order.size()-1;i>=0;i--){
        int curr = order[i];
        sizeArr[curr] = 1;
        for(int nb: tree[curr]){
            if(nb == parentv[curr]) continue;
            sizeArr[curr] += sizeArr[nb];
            if(sizeArr[nb] > sizeArr[heavynode[curr]]){
                heavynode[curr] = nb;
            }
        }
    }
}

void hld_iter(const vector<vector<int>> &tree, int root){
    // stack of (node, chainHead) to process light-children later
    stack<pair<int,int>> st;
    st.push({root, root});
    while(!st.empty()){
        auto pr = st.top(); st.pop();
        int curr = pr.first;
        int chain = pr.second;
        while(curr != 0){
            headv[curr] = chain;
            lt[idxGlob] = val[curr];
            assidx[curr] = idxGlob;
            idxGlob++;
            int heavy = heavynode[curr];
            // push light children to stack to process later (their own chains)
            for(int nb : tree[curr]){
                if(nb == parentv[curr] || nb == heavy) continue;
                st.push({nb, nb});
            }
            curr = heavy; // continue down heavy child on same chain
        }
    }
}

void solve(){
    int n,q;
    cin >> n >> q;
    val.assign(n+1, 0);
    for(int i=1;i<=n;i++) cin >> val[i];
    vector<vector<int>> tree(n+1);
    for(int i=1;i<n;i++){
        int u,v; cin >> u >> v;
        tree[u].push_back(v);
        tree[v].push_back(u);
    }

    parentv.assign(n+1, 0);
    depthv.assign(n+1, 0);
    sizeArr.assign(n+1, 0);
    heavynode.assign(n+1, 0);

    dfs_iter(tree, 1);

    headv.assign(n+1, 0);
    assidx.assign(n+1, 0);
    lt.assign(n+1, 0);
    idxGlob = 1;
    hld_iter(tree, 1);

    vector<long long> arr(n+1);
    for(int i=1;i<=n;i++) arr[i] = lt[i];
    SegIter sg;
    sg.init(n);
    sg.build(arr);

    // buffer outputs using to_chars
    string out;
    out.reserve((size_t)q * 12);

    while(q--){
        int qtype; cin >> qtype;
        if(qtype == 1){
            int node; long long newval; cin >> node >> newval;
            int pos = assidx[node];
            sg.update(pos, newval);
            continue;
        }
        int a,b; cin >> a >> b;
        long long ans = 0; // preserved logic: original code used 0 as start
        while(headv[a] != headv[b]){
            if(depthv[headv[a]] < depthv[headv[b]]){
                int t=a; a=b; b=t;
            }
            long long qres = sg.rangeMax(assidx[headv[a]], assidx[a]);
            ans = max(ans, qres);
            a = parentv[headv[a]];
        }
        if(depthv[a] < depthv[b]) { int t=a; a=b; b=t; }
        long long qres = sg.rangeMax(assidx[b], assidx[a]);
        ans = max(ans, qres);

        // append ans to out via to_chars
        char buf[32];
        auto [ptr, ec] = std::to_chars(buf, buf+32, ans);
        out.append(buf, ptr);
        out.push_back(' ');
    }

    if(!out.empty() && out.back() == ' ') out.pop_back();
    out.push_back('\n');
    cout << out;
}

int main(){
    FAST_IO
    int tests = 1;
    while(tests--) solve();
    return 0;
}
