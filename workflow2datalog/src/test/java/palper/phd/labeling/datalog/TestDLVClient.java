package palper.phd.labeling.datalog;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class TestDLVClient {

	@Test
	public void test() {
		DLVClient cli;
		try {
			cli = new DLVClient();

			cli.invokeDlv(null, null);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
