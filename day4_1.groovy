def sampleInput = '''MMMSXXMASM
MSAMXMSMSA
AMXSXMAAMM
MSAMASMSMX
XMASAMXAMM
XXAMMXXAMA
SMSMSASXSS
SAXAMASAAA
MAMMMXMMMM
MXMXAXMASX
'''

static long xmasCount(String input) {
    input.count('XMAS')
}

static List<String> transpose(List<String> input) {
    int n = input.size()
    List<String> output = new ArrayList<>(input[0].size())
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < input[i].size(); j++) {
            output[j] += input[i][j]
        }
    }
    output
}

static long xmasHorizontalCount(List<String> input) {
    input.collect { xmasCount(it) + xmasCount(it.reverse()) }.sum()
}

static long xmasDiagonalCheck(List<String> input, int x, int y) {
    int n = input.size()
    int m = input[0].size()
    def dirs = [[1, 1], [1, -1], [-1, 1], [-1, -1]]
    def XMAS = 'XMAS'
    dirs.collect {
        for(int k = 1; k <= 3; k++) {
            def xp = x + k*it[0]
            def yp = y + k*it[1]
            if(xp >= 0 && xp < n && yp >= 0 && yp < m && input[xp][yp] == XMAS[k]) continue
            return 0
        }
        return 1
    }.sum()
}

static long xmasDiagonalCount(List<String> input) {
    def sum = 0
    for (int i = 0; i < input.size(); i++) {
        for (int j = 0; j < input[i].size(); j++) {
            if(input[i][j] == 'X') sum += xmasDiagonalCheck(input, i, j)
        }
    }
    sum
}

static long solve(List<String> input) {
    xmasHorizontalCount(input) + xmasHorizontalCount(transpose(input)) +
            xmasDiagonalCount(input)
}

assert 18L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day4.txt').readLines())