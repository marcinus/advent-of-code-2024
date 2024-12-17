def sampleInput = '''Register A: 729
Register B: 0
Register C: 0

Program: 0,1,5,4,3,0'''

static def parse(List<String> input) {
    [
            (input[0] - 'Register A: ') as int,
            (input[1] - 'Register B: ') as int,
            (input[2] - 'Register C: ') as int,
            (input[4] - 'Program: ').split(',').collect { it as int } as int[]
    ]
}

static String emulate(long A, long B, long C, int[] P) {
    int IP = 0
    def out = new StringBuilder()
    while (IP >= 0 && IP < P.length) {
        int I = P[IP]
        int op = P[IP+1]
        long opVal = (op < 4 ? op : (op == 4 ? A : (op == 5 ? B : C)))
        println "$IP $I, $op, $opVal, $A, $B, $C"
        switch (I) {
            case 0: // adv
                A >>= opVal
                break
            case 1: // bxl
                B ^= op // Note: literal operand
                break
            case 2: // bst
                B = opVal & 0b111
                break
            case 3: // jnz
                if (A != 0) {
                    IP = op
                }
                break
            case 4: // bxc
                B ^= C
                break
            case 5: // out
                out.append(opVal & 0b111)
                println out
                break
            case 6: // bdv
                B = (A >> opVal)
                break
            case 7: // cdv
                C = (A >> opVal)
                break
            default:
                throw new RuntimeException("Invalid instruction at $IP: ${P[IP]}")
        }
        if (I != 3 || A == 0) { // JNZ
            IP += 2
        }
    }
    (0..<out.size()).collect { out[it] }.join(',')
}

static String solve(List<String> input) {
    def (A, B, C, P) = parse(input)
    emulate(A, B, C, P)
}

assert '4,6,3,5,6,3,5,2,1,0' == solve(sampleInput.split('\n') as List)
println solve(new File('input/day17.txt').readLines())