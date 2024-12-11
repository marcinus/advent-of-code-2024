import groovy.transform.Memoized

def sampleInput = '''125 17'''

static List<Long> splitDigits(long element) {
    int digitsNo = digits(element)
    long divider = 10 ** (digitsNo / 2)
    long leftPart = element / divider
    long rightPart = element % divider
    [leftPart, rightPart]
}

static int digits(long element) {
    if (element == 0) return 1
    int digits = 0
    while(element > 0) {
        element /= 10
        digits += 1
    }
    digits
}

@Memoized
static long produces(long input, int iterations) {
    if(iterations == 0) return 1
    if (input == 0) return produces(1, iterations-1)
    if (digits(input) % 2 == 0) {
        def (digitsA, digitsB) = splitDigits(input)
        return produces(digitsA, iterations-1) + produces(digitsB, iterations-1)
    }
    return produces(input*2024, iterations-1)
}

static long solve(String input, int iterations = 75) {
    def list = input.split(' ').collect { it as long }
    list.collect { produces(it, iterations) }.sum() as long
}

assert 55312L == solve(sampleInput, 25)
println solve(new File('input/day11.txt').text)