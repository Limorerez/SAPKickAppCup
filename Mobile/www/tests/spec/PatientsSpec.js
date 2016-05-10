describe('Patients Test', function() {
	var allUsersCount;
	
	it('Gets all users', function() {
		runTestAuthenticated(function() {
			var result = getPatients();
			var data = JSON.parse(result.data);
			preTestsUsersCount = data.length - 1;
			expect(result.status).toBe(Response.SUCCESS)
		});
		logout();
	});

	it('Creates a new user', function() {
		runTestAuthenticated(function() {
			var result = getPatients();
			var data = JSON.parse(result.data);
			var lastPatient = data[data.length - 1];
			stripPatientServerGeneratedData(lastPatient);
			lastPatient.password = TestData.patient.password;
			expect(data.length).toBe(preTestsUsersCount + 1);
			expect(lastPatient).toEqual(TestData.patient);
		});
		logout();
	});

	it('Creates the user again (should already exist)', function() {
		runTestAuthenticated(function() {
			var result = createPatient(TestData.patient);
			var data = JSON.parse(result.data);
			expect(result.status).toBe(Response.SUCCESS);
			expect(data.status).toBe("USER_EXISTS");

			result = getPatients();
			data = JSON.parse(result.data);
			expect(data.length).toBe(preTestsUsersCount + 1);
		});
		logout();
	});
});
