const nodemailer = require('nodemailer');

// Replace with your actual credentials
const senderEmail = 'ygspaces@gmail.com';
const senderPassword = 'lona sorm mvjf eiqi';
const adminEmail = 'robel4872@gmail.com';

let transporter = nodemailer.createTransport({
    service: 'gmail',
    auth: {
        user: senderEmail,
        pass: senderPassword,
    },
});

let mailOptions = {
    from: senderEmail,
    to: adminEmail,
    subject: 'Test Email from Nodemailer',
    html: '<p>This is a test email sent from a Node.js script.</p>',
};

transporter.sendMail(mailOptions, (error, info) => {
    if (error) {
        return console.log('Error sending email:', error);
    }
    console.log('Email sent:', info.response);
});
