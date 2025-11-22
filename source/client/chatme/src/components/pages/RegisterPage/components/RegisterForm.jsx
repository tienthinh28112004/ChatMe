export const RegisterForm = (props) =>{
    const {
        handleRegisterSubmit,
        errorMessage,
        formData,
        handleInputChange,
        handleInputBlur,
        formErrors,
    } = props
    return (
        <div className="row justify-content-center">
            <div className="col-12 col-lg-10 col-xl-8">
                <div className="row gy-5 justify-content-center">
                    <div className="col-12 col-lg-8">
                        <form onSubmit={handleRegisterSubmit}>
                            <div className="row gy-3 overflow-hidden">
                                <div className="col-12">
                                    <div className="form-floating mb-3">
                                        <input 
                                            type="email"
                                            className="form-control"
                                            name="email"
                                            id="email"
                                            placeholder="name@exmaple"
                                            required
                                            value={formData.email}
                                            onChange={handleInputChange}
                                            onBlur={handleInputBlur}
                                        />
                                        <label htmlFor="email" className="form-label">
                                            Email
                                        </label>
                                        {formErrors.email && (
                                            <p className="text-danger">{formErrors.email}</p>
                                        )}
                                    </div>
                                </div>
                                <div className="col-12">
                                    <div className="form-floating mb-3">
                                        <input 
                                            type="password"
                                            className="form-control"
                                            name="password"
                                            id="password"
                                            placeholder="Password"
                                            required
                                            value={formData.password}
                                            onChange={handleInputChange}
                                            onBlur={handleInputBlur}
                                        />
                                        <label htmlFor="password" className="form-label">
                                            Password
                                        </label>
                                        {formErrors.password && (
                                            <p className="text-danger">{formErrors.password}</p>
                                        )}
                                    </div>
                                </div>
                                <div className="col-12">
                                    <div className="form-floating mb-3">
                                        <input 
                                            type="text"
                                            className="form-control"
                                            name="fullName"
                                            id="fullName"
                                            placeholder="First Name"
                                            required
                                            value={formData.fullName}
                                            onChange={handleInputChange}
                                            onBlur={handleInputBlur}
                                        />
                                        <label htmlFor="fullName" className="form-label">
                                            Full Name
                                        </label>
                                        {formErrors.fullName && (
                                            <p className="text-danger">{formErrors.fullName}</p>
                                        )}
                                    </div>
                                </div>
                                <div className="col-12">
                                    <div className="d-grid">
                                        <button 
                                            className="btn btn-lg btn-dark rounded-0 fs-6"
                                            type="submit"
                                        >
                                            Register
                                        </button>
                                    </div>
                                </div>
                                {errorMessage && (
                                    <p className="text-danger text-center">{errorMessage}</p>
                                )}
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    );
};