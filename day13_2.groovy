def sampleInput = '''Button A: X+94, Y+34
Button B: X+22, Y+67
Prize: X=8400, Y=5400

Button A: X+26, Y+66
Button B: X+67, Y+21
Prize: X=12748, Y=12176

Button A: X+17, Y+86
Button B: X+84, Y+37
Prize: X=7870, Y=6450

Button A: X+69, Y+23
Button B: X+27, Y+71
Prize: X=18641, Y=10279'''


static def findSolution(A, B, P) {
    def pX = (B[1] * P[0] - B[0] * P[1]) as BigInteger
    def pY = (A[1] * P[0] - A[0] * P[1]) as BigInteger
    def xyC = (A[1] * B[0] - A[0] * B[1]) as BigInteger
    println "$pX $pY $xyC"
    println([-pX / xyC, pY / xyC])
    if (-pX.remainder(xyC) == 0 && pY.remainder(xyC) == 0) return [-pX / xyC, pY / xyC]
    [0, 0]
}

static def parse(List<String> input) {
    def BUTTON_REGEX = ~/Button (?:A|B): X\+(\d+), Y\+(\d+)/
    def PRIZE_REGEX = ~/Prize: X=(\d+), Y=(\d+)/

    input.collate(4).collect {
        def buttonA = (it[0] =~ BUTTON_REGEX)[0]
        def buttonB = (it[1] =~ BUTTON_REGEX)[0]
        def prize = (it[2] =~ PRIZE_REGEX)[0]
        [
                [buttonA[1] as BigInteger, buttonA[2] as BigInteger],
                [buttonB[1] as BigInteger, buttonB[2] as BigInteger],
                [(prize[1] as BigInteger) + 10000000000000, (prize[2] as BigInteger) + 10000000000000]
        ]
    }
}

static long solve(List<String> input) {
    def data = parse(input)
    data.collect {
        println it
        def (A, B) = findSolution(it[0], it[1], it[2])
        A * 3 + B
    }.sum() as long
}

assert 875318608908L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day13.txt').readLines())