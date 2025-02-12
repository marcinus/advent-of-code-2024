def sampleInput = '''190: 10 19
3267: 81 40 27
83: 17 5
156: 15 6
7290: 6 8 6 15
161011: 16 10 13
192: 17 8 14
21037: 9 7 18 13
292: 11 6 16 20'''

static boolean feasible(long target, def elements, def current, def pos = 1) {
    if(current > target) return false
    if(pos == elements.size()) return current == target
    return feasible(target, elements, current*elements[pos], pos+1) || feasible(target, elements, current+elements[pos], pos+1)
}

static boolean feasible(long target, def elements) {
    feasible(target, elements, elements[0])
}

static long solve(List<String> input) {
    input.collect {
        def data = it.split(': ')
        [data[0] as long, data[1].split(' ').collect {it as long}]
    }.collect {
        feasible(it[0], it[1]) ? it[0] : 0
    }.sum()
}

assert 3749L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day7.txt').readLines())