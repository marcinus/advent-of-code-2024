def sampleInput = '''1
2
3
2024'''

static def simulate(long secret, int steps) {
    [secret % 10] + (0..<steps).collect {
        secret = (secret ^ (secret << 6)) % 16777216L
        secret = (secret ^ (secret >> 5)) % 16777216L
        secret = (secret ^ (secret << 11)) % 16777216L
        secret % 10
    }
}

static def sequencesToPrices(List<Long> prices) {
    def changes = (1..<prices.size()).collect { i ->
        prices[i] - prices[i-1]
    }
    def lookup = [:]
    (4..<prices.size()).each { i ->
        def sequence = [changes[i-4], changes[i-3], changes[i-2], changes[i-1]]
        if(!lookup.containsKey(sequence)) {
            lookup[sequence] = prices[i]
        }
    }
    lookup
}

static long solve(List<String> input) {
    def sequencesToTotalGains = [:].withDefault { 0L }
    input.each {
        def prices = simulate(it as long, 2000)
        sequencesToPrices(prices).each { sequence, price ->
            sequencesToTotalGains[sequence] += price
        }
    }
    sequencesToTotalGains.values().max()
}

assert 23L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day22.txt').readLines())