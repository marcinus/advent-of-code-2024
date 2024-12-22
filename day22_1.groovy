def sampleInput = '''1
10
100
2024'''

static long simulate(long secret, int steps) {
    steps.times {
        secret = (secret ^ (secret << 6)) % 16777216L
        secret = (secret ^ (secret >> 5)) % 16777216L
        secret = (secret ^ (secret << 11)) % 16777216L
    }
    secret
}

static long solve(List<String> input) {
    input.collect {
        simulate(it as long, 2000)
    }.sum() as long
}


assert 37327623L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day22.txt').readLines())