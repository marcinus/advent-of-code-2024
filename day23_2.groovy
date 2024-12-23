import groovy.transform.Memoized

def sampleInput = '''ka-co
ta-co
de-co
ta-ka
de-ta
ka-de'''

@Memoized
static def largestClique(def input, Set current, Set subsetConsidered) {
    def remaining = subsetConsidered - current
    remaining.findAll { A -> current.every { input[it].contains(A) }}.collect { A ->
        largestClique(input, current + A, subsetConsidered)
    }.max { it.size() } ?: current
}

static def largestClique(def input) {
    def subsetConsidered = input.keySet() as Set
    input.keySet().withIndex().collect { it, i ->
        def largest = largestClique(input, [it] as Set, subsetConsidered.intersect(input[it]))
        subsetConsidered -= it
        largest
    }.max { it.size() }
}

static String solve(List<String> input) {
    def lookup = [:].withDefault { [] as Set }
    input.each { line ->
        def (a, b) = line.split('-')
        lookup[a] << b
        lookup[b] << a
    }
    println lookup.size()
    largestClique(lookup).sort().join(',')
}

assert 'co,de,ka,ta' == solve(sampleInput.split('\n') as List)
println solve(new File('input/day23.txt').readLines())