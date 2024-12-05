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

static boolean checkLetter(String letter, List<String> input, int x, int y) {
    x >= 0 && x < input.size() &&
    y >= 0 && y < input[0].size() &&
    input[x][y] == letter
}

static boolean checkDiagonal(List<String> input, List<List<Integer>> diagonal) {
    [diagonal, diagonal.reverse()].any { diag ->
        checkLetter('M', input, diag[0][0], diag[0][1]) &&
        checkLetter('S', input, diag[1][0], diag[1][1])
    }
}

static boolean xmasDiagonalCheck(List<String> input, int x, int y) {
    checkDiagonal(input, [[x-1, y-1], [x+1, y+1]]) &&
    checkDiagonal(input, [[x-1, y+1], [x+1, y-1]])
}

static long xmasDiagonalCount(List<String> input) {
    def sum = 0
    for (int i = 0; i < input.size(); i++) {
        for (int j = 0; j < input[i].size(); j++) {
            if(input[i][j] == 'A') sum += xmasDiagonalCheck(input, i, j) ? 1 : 0
        }
    }
    sum
}

static long solve(List<String> input) {
    xmasDiagonalCount(input)
}

assert 9L == solve(sampleInput.split('\n') as List)
println solve(new File('input/day4.txt').readLines())