import groovy.transform.Memoized

def sampleInput = '''r, wr, b, g, bwu, rb, gb, br

brwrr
bggr
gbbr
rrbgbr
ubwu
bwurrg
brgr
bbrgwb'''

static def parse(List<String> input) {
    Set<String> ribbons = input[0].split(', ')
    List<String> patterns = input.drop(2)
    [ribbons, patterns]
}

@Memoized
static long waysToCreate(String pattern, Set<String> ribbons) {
    (ribbons.contains(pattern) ? 1 : 0) + ((1..<(pattern.size())).sum {
        def part1 = pattern.substring(0, it)
        def part2 = pattern.substring(it)
        ribbons.contains(part1) ? waysToCreate(part2, ribbons) : 0L
    } ?: 0) as long
}

static long solve(List<String> input) {
    def (ribbons, patterns) = parse(input)
    patterns.collect {
        waysToCreate(it, ribbons)
    }.sum() as long
}


assert 16L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day19.txt').readLines())