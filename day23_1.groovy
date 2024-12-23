def sampleInput = '''kh-tc
qp-kh
de-cg
ka-co
yn-aq
qp-ub
cg-tb
vc-aq
tb-ka
wh-tc
yn-cg
kh-ub
ta-co
de-co
tc-td
tb-wq
wh-td
ta-ka
td-qp
aq-cg
wq-ub
ub-vc
de-ta
wq-aq
wq-vc
wh-yn
ka-de
kh-ta
co-tc
wh-qp
tb-vc
td-yn'''

static long solve(List<String> input) {
    def lookup = [:].withDefault { [] as Set }
    input.each { line ->
        def (a, b) = line.split('-')
        lookup[a] << b
        lookup[b] << a
    }
    def setsOfThreeWithT = [] as Set
    lookup.findAll { it.key.startsWith('t') }.each { A, Bs ->
        Bs.findAll { it != A }.each { B ->
            // compare the smaller of
            def CsFromB = lookup[B]
            if(CsFromB.size() < Bs.size()-1) {
                CsFromB.findAll { it != A && it != B }.each{ C ->
                    setsOfThreeWithT << ([A, B, C] as Set)
                }
            } else {
                Bs.findAll { it != A && it != B && CsFromB.contains(it) }.each { C ->
                    setsOfThreeWithT << ([A, B, C] as Set)
                }
            }
        }
    }
    setsOfThreeWithT.each { println it }
    setsOfThreeWithT.size()
}

assert 7L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day23.txt').readLines())