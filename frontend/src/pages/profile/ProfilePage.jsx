import { Edit, Facebook, Linkedin, Instagram, X } from "lucide-react";

const user = {
  name: "Musharof Chowdhury",
  job: "Team Manager",
  location: "Arizona, United States",
  avatar: "https://randomuser.me/api/portraits/men/75.jpg",
  email: "randomuser@pimjo.com",
  phone: "+09 363 398 46",
  bio: "Team Manager",
  country: "United States.",
  city: "Phoenix, Arizona, United States.",
  postal: "ERT 2489",
  tax: "AS4568384",
};

export default function ProfilePage() {
  return (
    <div className="p-6 lg:p-10 max-w-6xl mx-auto">
      {/* Profile Card */}
      <div className="bg-white rounded-2xl shadow-sm border border-gray-100 mb-6 p-6">
        <h2 className="text-xl font-bold mb-5">Profile</h2>
        <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-6">
          {/* Left: Avatar, Name */}
          <div className="flex items-center gap-5">
            <img src={user.avatar} className="w-20 h-20 rounded-full object-cover border-2 border-gray-100" />
            <div>
              <div className="text-xl font-bold">{user.name}</div>
              <div className="text-gray-500">{user.job} <span className="mx-2">|</span> {user.location}</div>
            </div>
          </div>
          {/* Social & Edit */}
          <div className="flex items-center gap-3">
            <a href="#" className="btn btn-circle btn-ghost border border-gray-200 text-gray-400"><Facebook size={20} /></a>
            <a href="#" className="btn btn-circle btn-ghost border border-gray-200 text-gray-400"><X size={20} /></a>
            <a href="#" className="btn btn-circle btn-ghost border border-gray-200 text-gray-400"><Linkedin size={20} /></a>
            <a href="#" className="btn btn-circle btn-ghost border border-gray-200 text-gray-400"><Instagram size={20} /></a>
            <button className="btn btn-outline rounded-full border-gray-200 flex items-center gap-2 px-4">
              <Edit size={16} /> Edit
            </button>
          </div>
        </div>
      </div>

      {/* Personal Info */}
      <div className="bg-white rounded-2xl shadow-sm border border-gray-100 mb-6 p-6">
        <div className="flex items-center justify-between mb-5">
          <h3 className="text-lg font-bold">Personal Information</h3>
          <button className="btn btn-outline rounded-full border-gray-200 flex items-center gap-2 px-4">
            <Edit size={16} /> Edit
          </button>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-y-4 gap-x-12">
          <div>
            <div className="font-semibold text-gray-500">First Name</div>
            <div className="text-base">{user.name.split(" ")[0]}</div>
          </div>
          <div>
            <div className="font-semibold text-gray-500">Last Name</div>
            <div className="text-base">{user.name.split(" ")[1]}</div>
          </div>
          <div>
            <div className="font-semibold text-gray-500">Email address</div>
            <div className="text-base">{user.email}</div>
          </div>
          <div>
            <div className="font-semibold text-gray-500">Phone</div>
            <div className="text-base">{user.phone}</div>
          </div>
          <div className="md:col-span-2">
            <div className="font-semibold text-gray-500">Bio</div>
            <div className="text-base">{user.bio}</div>
          </div>
        </div>
      </div>

      {/* Address */}
      <div className="bg-white rounded-2xl shadow-sm border border-gray-100 mb-6 p-6">
        <div className="flex items-center justify-between mb-5">
          <h3 className="text-lg font-bold">Address</h3>
          <button className="btn btn-outline rounded-full border-gray-200 flex items-center gap-2 px-4">
            <Edit size={16} /> Edit
          </button>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-y-4 gap-x-12">
          <div>
            <div className="font-semibold text-gray-500">Country</div>
            <div className="text-base">{user.country}</div>
          </div>
          <div>
            <div className="font-semibold text-gray-500">City/State</div>
            <div className="text-base">{user.city}</div>
          </div>
          <div>
            <div className="font-semibold text-gray-500">Postal Code</div>
            <div className="text-base">{user.postal}</div>
          </div>
          <div>
            <div className="font-semibold text-gray-500">TAX ID</div>
            <div className="text-base">{user.tax}</div>
          </div>
        </div>
      </div>
    </div>
  );
}
