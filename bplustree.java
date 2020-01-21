import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;

public class bplustree {

	public static void main(String[] args) throws Exception {

		/*
		 * Instantiating the class
		 */

		BPlus_Tree<Integer, Double> b = new BPlus_Tree<Integer, Double>();

		/*
		 * File reader to read input from file
		 */
		File input_file = new File(args[0]);

		/*
		 * File writer to write output to the file
		 */

		File fileName = new File("output_file.txt");

		BufferedReader br = new BufferedReader(new FileReader(input_file));
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

		String st;

		int dual_tokens = 0;

		while ((st = br.readLine()) != null) {
			// "(" delimiter to identify the operation from the input file
			StringTokenizer st1 = new StringTokenizer(st, "(");
			String operation = st1.nextToken();

			String count_tk = st1.nextToken();
			String[] ip_values_precheck = count_tk.split(",");

			/*
			 * flags to check if it is insert or other operation based on the number of
			 * arguments
			 */
			if (ip_values_precheck.length == 2) {
				dual_tokens = 1;
			} else {
				dual_tokens = 0;
			}

			/*
			 * It is either insert or search within a range operation
			 */
			if (dual_tokens == 1) {
				String[] ip_values = ip_values_precheck;
				String insert_key = ip_values[0];

				StringTokenizer st2 = new StringTokenizer(ip_values[1], ")");
				String insert_value = st2.nextToken().trim();

				// Inserting into the B+ Tree
				if (operation.equals("Insert")) {
					b.insert(Integer.parseInt(insert_key), Double.parseDouble(insert_value));
				}

				// Searching Key,Value pairs within the range specified
				else {
					int i = Integer.parseInt(insert_key);
					int cnt = Integer.parseInt(insert_value);
					for (; i < cnt; i++) {
						if (b.search(i) != null) {
							writer.write(b.search(i) + ",");
						}
					}
					writer.newLine();
				}
			}

			/*
			 * It is either initialize, delete or search
			 */

			else {
				String op_key = count_tk;
				String op_key_parsed = op_key.substring(op_key.indexOf("(") + 1, op_key.indexOf(")"));

				// Perform delete operation on the key specified
				if (operation.equals("Delete")) {
					b.delete(Integer.parseInt(op_key_parsed));
				}

				// search for the key,value pair specified
				else if (operation.equals("Search")) {
					writer.write(b.search(Integer.parseInt(op_key_parsed)) + "");
					writer.newLine();
				}
			}
		}
		br.close();
		writer.close();
	}
}
