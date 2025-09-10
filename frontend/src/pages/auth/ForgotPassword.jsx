const ForgotPassword = () => (
  <div className="flex min-h-screen items-center justify-center bg-base-200">
    <div className="card w-full max-w-md bg-base-100 shadow-xl">
      <div className="card-body">
        <h2 className="text-xl font-bold mb-4">Forgot Password</h2>
        <form>
          <div className="form-control mb-2">
            <label className="label">
              <span className="label-text font-semibold">Email</span>
            </label>
            <input type="email" className="input input-bordered w-full" placeholder="your@email.com" required />
          </div>
          <button className="btn btn-primary w-full">Reset Password</button>
        </form>
        <a href="/login" className="link link-primary mt-4 block text-center">Back to Login</a>
      </div>
    </div>
  </div>
);

export default ForgotPassword;
