import groovy.transform.Memoized

def sampleInput = '''x00: 1
x01: 0
x02: 1
x03: 1
x04: 0
y00: 1
y01: 1
y02: 1
y03: 1
y04: 1

ntg XOR fgs -> mjb
y02 OR x01 -> tnw
kwq OR kpj -> z05
x00 OR x03 -> fst
tgd XOR rvg -> z01
vdt OR tnw -> bfw
bfw AND frj -> z10
ffh OR nrd -> bqk
y00 AND y03 -> djm
y03 OR y00 -> psh
bqk OR frj -> z08
tnw OR fst -> frj
gnj AND tgd -> z11
bfw XOR mjb -> z00
x03 OR x00 -> vdt
gnj AND wpb -> z02
x04 AND y00 -> kjc
djm OR pbm -> qhw
nrd AND vdt -> hwm
kjc AND fst -> rvg
y04 OR y02 -> fgs
y01 AND x02 -> pbm
ntg OR kjc -> kwq
psh XOR fgs -> tgd
qhw XOR tgd -> z09
pbm OR djm -> kpj
x03 XOR y03 -> ffh
x00 XOR y04 -> ntg
bfw OR bqk -> z06
nrd XOR fgs -> wpb
frj XOR qhw -> z04
bqk OR frj -> z07
y03 OR x01 -> nrd
hwm AND bqk -> z03
tgd XOR rvg -> z12
tnw OR pbm -> gnj'''

static def parse(String input) {
    def RULE_PATTERN = ~/([a-z0-9]{3}) (XOR|OR|AND) ([a-z0-9]{3}) -> ([a-z0-9]{3})/
    def (init, rules) = input.split('\n\n')
    def initMap = init.split('\n').collectEntries {
        def (id, value) = it.split(': ')
        [(id) : value == '1']
    }
    def ruleMap = rules.split('\n').collectEntries {
        def match = it =~ RULE_PATTERN
        [
                (match[0][4]): [
                        match[0][2],
                        match[0][1],
                        match[0][3]
                ]
        ]
    }
    [initMap, ruleMap]
}

static void eval(Map variables, Map rules, String target, List operation) {
    println operation
    def operands = operation.drop(1).collect {
        if (!variables.containsKey(it)) {
            assert rules.containsKey(it)
            eval(variables, rules, it, rules[it])
        }
        variables[it]
    }
    switch (operation[0]) {
        case 'XOR':
            variables[target] = operands[0] ^ operands[1]
            break
        case 'OR':
            variables[target] = operands[0] || operands[1]
            break
        case 'AND':
            variables[target] = operands[0] && operands[1]
            break
        default:
            throw new IllegalStateException("Invalid operation name ${operation[1]}")
    }
    println "Evaluated $target to ${variables[target]} with $operands and ${operation[0]}"
}

static long solve(String input) {
    def (initMap, ruleMap) = parse(input)
    ruleMap.each { target, operation ->
        eval(initMap, ruleMap, target, operation)
    }
    initMap.toSorted().each {println it}
    initMap.findAll { it.key.startsWith('z') }.toSorted().inject(0L) { total, element ->
        println element
        println (element.value ? (1 << ((element.key - 'z') as int)) : 0L)
        total | (element.value ? (1L << ((element.key - 'z') as int)) as long : 0L)
    } as long
}

assert 2024L == solve(sampleInput)
println solve(new File('input/day24.txt').text)