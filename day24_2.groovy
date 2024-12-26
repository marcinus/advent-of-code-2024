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
        [(id): id]//value == '1']
    }
    def ruleMap = rules.split('\n').collectEntries {
        def match = it =~ RULE_PATTERN
        [
                (match[0][4]): [
                        match[0][2],
                        [match[0][1],
                        match[0][3]] as Set
                ]
        ]
    }
    [initMap, ruleMap]
}

static String findSingleRule(Map ruleMap, String operation, List operands) {
    def matchingRules = ruleMap.findAll { k, v-> v == [operation, operands as Set]}
    println "Finding $operation with $operands yields ${matchingRules.size()}"
    assert matchingRules.size() <= 1 // means we have unique match
    matchingRules.find { true }?.key
}

static String findXOR(Map ruleMap, List operands) {
    findSingleRule(ruleMap, 'XOR', operands)
}

static String findAnd(Map ruleMap, List operands) {
    findSingleRule(ruleMap, 'AND', operands)
}

static String findOr(Map ruleMap, List operands) {
    findSingleRule(ruleMap, 'OR', operands)
}

static String findSumSimple(Map ruleMap, List operands) {
    def prev = operands[0]
    for(int i = 1; i < operands.size(); i++) {
        def next = operands[i]
        def xor = findXOR(ruleMap, [prev, next])
        if(xor) {
            prev = xor
        } else {
            return null
        }
    }
    return prev
}

static String findSum(Map ruleMap, String carry, List operands) {
    // Option 1: (c^x)^y
    [
            [carry, operands[0], operands[1]],
            [carry, operands[1], operands[0]],
            [operands[0], operands[1], carry]
    ].collect { findSumSimple(ruleMap, it) }.find { it != null }
}

static String findCarry(Map ruleMap, String carry, List operands) {

}

static void swap(Map input, String k1, String k2) {
    def temp = input[k1]
    input[k1] = input[k2]
    input[k2] = temp
}

static String solve(String input) {
    def (initMap, ruleMap) = parse(input)
    int bits = (initMap.keySet().size() / 2)
    def swaps = [
            'z06', 'hwk',
            'qmd', 'tnt',
            'z31', 'hpc',
            'z37', 'cgr'
    ]
    swaps.collate(2).each { swap(ruleMap, it[0], it[1]) }
    String carry
    bits.times { i ->
        if (i == 0) {
            assert findXOR(ruleMap, ['x00', 'y00']) == 'z00'
            carry = findAnd(ruleMap, ['x00', 'y00'])
        } else {
            def (xi, yi, zi) = i < 10 ? ["x0$i", "y0$i", "z0$i"] : ["x$i", "y$i", "z$i"]
            def ithOperands = [xi, yi]
            // Now we're optimistic that the rules follow the same pattern
            assert zi == findSum(ruleMap, carry, ithOperands)
            if(i == bits-1) return
            def and = findAnd(ruleMap, ithOperands)
            def xor = findXOR(ruleMap, ithOperands)
            def carryAndOr = findAnd(ruleMap, [carry, xor])
            carry = findOr(ruleMap, [carryAndOr, and])
        }
    }
    swaps.sort().join(',')
}

//assert 'z00,z01,z02,z05' == solve(sampleInput)
println solve(new File('input/day24.txt').text)