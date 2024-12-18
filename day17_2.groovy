def sampleInput = '''Register A: 2024
Register B: 0
Register C: 0

Program: 0,3,5,4,3,0'''

static def parse(List<String> input) {
    [
            (input[0] - 'Register A: ') as int,
            (input[1] - 'Register B: ') as int,
            (input[2] - 'Register C: ') as int,
            (input[4] - 'Program: ').split(',').collect { it as int } as int[]
    ]
}

static Map<Integer, Boolean> solveSimpleEquation(String equation) {
    def PATTERN = ~/^\(A >> (\d+)\) & 0b111 == (\d)$/
    def match = (equation =~ PATTERN)
    if (match) {
        int i = match[0][1] as int
        int x = match[0][2] as int
        return [
                (i)    : (x & 1) as boolean,
                (i + 1): (x & 2) as boolean,
                (i + 2): (x & 4) as boolean,
        ]
    }
    [:]
}

static Map<Integer, Boolean> joinSolutions(List<Map<Integer, Boolean>> solutions) {
    Map<Integer, Boolean> joinedSolution = [:]
//    solutions.each {
//        println it
//    }
    solutions.each {
        it.each { i, b ->
            if (joinedSolution.containsKey(i) && joinedSolution[i] != b) {
                throw new RuntimeException("Contradicting solution $i: $b ${joinedSolution[i]}")
            }
            joinedSolution[i] = b
        }
    }
    joinedSolution
}

static void validateAgainstZeroInequalities(Map<Integer, Boolean> solution, List<String> inequalities) {
    def PATTERN = ~/^\(A >> (\d+)\) != 0$/
    assert inequalities.findAll { it ==~ PATTERN }.every {
        def match = (it =~ PATTERN)
        int i = match[0][1]
        (i..<64).any {
            solution[it] != false
        }
    }
}

static def validateAgainstZeroEquations(Map<Integer, Boolean> solution, List<String> equations) {
    def PATTERN = ~/^\(A >> (\d+)\) == 0$/
    assert equations.findAll { it ==~ PATTERN }.every {
        def match = (it =~ PATTERN)
        int i = match[0][1]
        (i..<64).every {
            !solution.containsKey(it) || solution[it] == false
        }
    }
}

static Map<Integer, Boolean> solveArrayOfSimpleEquations(List<String> equations) {
    def solutions = equations.collect {solveSimpleEquation(it) }
    joinSolutions(solutions)
}

static def parseComplexEquation(String complexEquation) {
    def PATTERN = ~/^\(\(\(A(?: >> (\d+))? & 0b111\) \^ 3\) \^ \(\(A(?: >> \d+)?\) >> \(\(A(?: >> \d+)? & 0b111\) \^ 5\)\)\) & 0b111 == (\d)$/
    def match = (complexEquation =~ PATTERN)
    if (match) {
        int i = (match[0][1] ?: 0) as int
        int x = match[0][2] as int
        return [i, x]
    }
    throw new RuntimeException("Unable to parse complex equation: $complexEquation")
}

static boolean solvesEquation(int j, int x, long A) {
    long k = (A >> j)
    int w = (k & 0b111)
    //println "${String.format("%03d", new BigInteger(Long.toBinaryString((long)w)))}"
    //println "\t${String.format("%03d", new BigInteger(Long.toBinaryString((long)w ^ 3)))}"
    //println "\t\t${String.format("%03d", new BigInteger(Long.toBinaryString((long)w ^ 5)))}"
    //println "\t\t\t${String.format("%03d", new BigInteger(Long.toBinaryString((long)((((k >> (w ^ 5))) & 0b111)))))}"
    //println "\t\t\t${String.format("%03d", new BigInteger(Long.toBinaryString((long)((((w ^ 3) ^ (k >> (w ^ 5))) & 0b111)))))}"
    //println "\t\t\t${String.format("%03d", new BigInteger(Long.toBinaryString((long)(x))))}"
    (((w ^ 3) ^ (k >> (w ^ 5))) & 0b111) == x
}

static def firstFeasible(List<List<Integer>> parameters, long A = 0L, int i = 0) {
    if (i == parameters.size()) return [A, true]
    def (j, x) = parameters[i]
    //println "Evaluating $i $j $x ${String.format("%048d", new BigInteger(Long.toBinaryString((long)A)))}"
    for(long k = 0; k < 8; k++) {
        long newA =  A ^ (k << j)
        //println "$i $k: New A: $newA"
        if (solvesEquation(j, x, newA)) {
            def f = firstFeasible(parameters, newA, i+1)
            if(f[1]) return f
        }
    }
    [A, false]
}

static long solveArrayOfComplexEquations(List<String> equations, List<String> verifications) {
    def parameters = equations.collect {parseComplexEquation(it) }.reverse()
    // Now the equations are in order
    firstFeasible(parameters)[0]
}

static long toLong(Map<Integer, Boolean> solution) {
    long output = 0
    solution.each { i, b ->
        if(b) {
            output |= (1 << i)
        }
    }
    output
}

static long solveValidateAndJoinSimpleEquations(List<String> equations, List<String> verifications) {
    def solution = solveArrayOfSimpleEquations(equations)
    validateAgainstZeroInequalities(solution, verifications)
    validateAgainstZeroEquations(solution, verifications)
    toLong(solution)
}

static int getMaximumNumberOfBits(List<String> verifications) {
    def PATTERN = ~/^\(A >> (\d+)\) == 0$/
    def v = verifications.find { it ==~ PATTERN }
    (v =~ PATTERN)[0][1] as int
}

static def parseP(int[] P) {
    int[] I = new int[P.length / 2]
    int[] O = new int[P.length / 2]
    0.step(P.size(), 2) { I[(it / 2) as int] = P[it]; O[(it / 2) as int] = P[it + 1] }

    [I, O]
}

static String simpleCompact(String expr) {
    def PATTERN = ~/^\(([A-C])\) >> \((\d+)\)$/
    def match = (expr =~ PATTERN)
    if (match) {
        return "${match[0][1]} >> ${(match[0][2] as int)}"
    }
    expr
}

static String compactXOR(String expr) {
    expr.replaceAll(~/\((.*) \^ (\d+)\) \^ (\d+)/) {
        "${it[1]} ^ ${(it[2] as long) ^ (it[3] as long)}"
    }
}

static String compactExpression(String expr) {
    expr = compactXOR(simpleCompact(expr))
    def PATTERN = ~/^\(\(?([A-C])\)? >> \(?(\d+)\)?\) >> \((\d+)\)$/
    def match = (expr =~ PATTERN)
    if (match) {
        return "${match[0][1]} >> ${(match[0][2] as int) + (match[0][3] as int)}"
    }
    expr
}

static def reverseEngineer(int B, int C, int[] P) {
    def (I, O) = parseP(P)
    def jnzPositions = I.findIndexValues { it == 3 }
    def outPositions = I.findIndexValues { it == 5 }

    // assumption: all 3 (jnz) instructions have its operand <= size(input)
    // the result is: the only way the program halts is via it's last command that need to end with +2.
    0.step(P.size(), 2) { assert P[it] != 3 || P[it + 1] < P.length }

    // Even stronger assumption: the only element of P that has 3 (jnz) is the last instruction, and it leads to the start of instruction set.
    assert jnzPositions.size() == 1 && jnzPositions[0] == I.size() - 1 && O[jnzPositions[0] as int] == 0

    // we will also assume there's a single 5 (out) instruction
    assert outPositions.size() == 1

    // so we can safely assume that either the program ends, or it works in a linear way.
    // Now, we are going to simulate the program, assuming that A = 0 at the end.
    // We can also assume that there will be exactly P.length passes of the program.

    String aExpr = 'A'
    String bExpr = B
    String cExpr = C

    def assertions = []
    def finishAssertions = []

    P.length.times { i ->
        I.length.times { j ->
            int cI = I[j]
            int op = O[j]
            def opVal = (op < 4 ? op : (op == 4 ? aExpr : (op == 5 ? bExpr : cExpr))) as String
            switch (cI) {
                case 0: // adv
                    aExpr = compactExpression("($aExpr) >> ($opVal)")
                    break
                case 1: // bxl
                    bExpr = "($bExpr) ^ $op"
                    break
                case 2: // bst
                    bExpr = "${compactExpression(opVal)} & 0b111"
                    break
                case 3: // jnz
                    if (i == P.length - 1) {
                        finishAssertions << "$aExpr == 0"
                    } else {
                        finishAssertions << "$aExpr != 0"
                    }
                    break
                case 4: // bxc
                    bExpr = "($bExpr) ^ ($cExpr)"
                    break
                case 5: // out
                    assertions << "(${compactExpression(opVal)}) & 0b111 == ${P[i]}"
                    break
                case 6: // bdv
                    bExpr = compactExpression("($aExpr) >> ($opVal)")
                    break
                case 7: // cdv
                    cExpr = compactExpression("($aExpr) >> ($opVal)")
                    break
                default:
                    throw new RuntimeException("Invalid instruction at $j: ${I[j]}")
            }
        }
    }

    // now, we assume every

//    assertions.each {
//        println it
//    }
//
//    finishAssertions.each {
//        println it
//    }

    def simple = solveValidateAndJoinSimpleEquations(assertions, finishAssertions)
    simple != 0 ? simple : solveArrayOfComplexEquations(assertions, finishAssertions)
}

static long solve(List<String> input) {
    def (A, B, C, P) = parse(input)
    reverseEngineer(B, C, P)
}


assert 117440L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day17.txt').readLines())