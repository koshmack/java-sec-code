def multiply_numbers(*numbers):
    result = 1
    for num in numbers:
        result *= num
    print("The multiplication result is:", result)

# Example usage:
multiply_numbers(2, 5, 3)  # Output: The multiplication result is: 30