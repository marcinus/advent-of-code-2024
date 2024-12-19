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
static boolean isPossible(String pattern, Set<String> ribbons) {
    if (ribbons.contains(pattern)) return true
    (1..(pattern.size()-1)).any {
        def part1 = pattern.substring(0, it)
        def part2 = pattern.substring(it)
        ribbons.contains(part1) && isPossible(part2, ribbons)
    }
}

static long solve(List<String> input) {
    def (ribbons, patterns) = parse(input)
    patterns.count {
        isPossible(it, ribbons)
    }
}


assert 6L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day19.txt').readLines())