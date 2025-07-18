def process_numbers(numbers):
    # Calculate sum of even numbers
    even_sum = sum(num for num in numbers if num % 2 == 0)
    # Calculate average of all numbers
    average = sum(numbers) / len(numbers) if numbers else 0
    print("Average of all numbers:", average)
    return even_sum

# Example usage:
input_str = input("Enter numbers separated by spaces: ")
numbers = list(map(int, input_str.split()))
result = process_numbers(numbers)
print("Sum of even numbers:", result)