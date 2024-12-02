def sampleInput = '''7 6 4 2 1
1 2 7 8 9
9 7 6 2 1
1 3 2 4 5
8 6 4 4 1
1 3 6 7 9
'''


static boolean isStrictlyIncreasing(int[] line) {
    for (int i = 1; i < line.length; i++) {
        if (line[i] <= line[i - 1]) return false;
    }
    true
}

static boolean isStrictlyDecreasing(int[] line) {
    for (int i = 1; i < line.length; i++) {
        if (line[i] >= line[i - 1]) return false;
    }
    true
}

static boolean isStrictlyMonotonic(int[] line) {
    isStrictlyIncreasing(line) || isStrictlyDecreasing(line)
}

static boolean isCompact(int[] line) {
    for (int i = 1; i < line.length; i++) {
        def diff = Math.abs(line[i] - line[i - 1])
        if (diff < 1 || diff > 3) return false
    }
    true
}

static boolean isSafe(int[] line) {
    isStrictlyMonotonic(line) && isCompact(line)
}

static long solve(List<String> lines) {
    def data = lines.collect { line ->
        line.split(' ')
    }
    data.count { line ->
        isSafe(line.collect { it as int } as int[])
    }
}

assert 2L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day2.txt').readLines())